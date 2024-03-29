package vcreature.morphology;

import com.jme3.math.Vector3f;
import vcreature.Being;
import vcreature.Environment;
import vcreature.collections.EvolveManager;
import vcreature.collections.Population;
import vcreature.genotype.Gene;
import vcreature.phenotype.Block;
import vcreature.utils.Statistics;

import java.util.ArrayList;
import java.util.Random;

import static vcreature.morphology.GeneticAlgorithmParams.*;
import static vcreature.morphology.GeneticStrategy.GA_TYPE;

/**
 * This class evolves a population of beings using a genetic algorithm (GA). The algorithms main functions are:
 * fitness calculation; selection; crossover; and mutation
 * @author Javier Chavez
 * @author Alex Baker
 * @author Dominic Salas
 * @author Cari Martinez
 */

public class GeneticAlgorithm
{
  private Environment environment;
  private CrossoverStrategy ga;
  private CrossoverMutation gam;
  private ArrayList<Being> nextGeneration;
  private Statistics statistics;
  private TournamentSelect select;
  private Random random = new Random();



  /**
   * @param environment reference to physics environment
   */
  public GeneticAlgorithm(Environment environment)
  {
    this.environment = environment;
    ga = new CrossoverStrategy();
    gam = new CrossoverMutation();
  }


  /**
   * This is the method that chooses what type of selection to use
   * then what type of GA to use.
   *
   * @param beings collection of beings
   * @param evolveManager
   * @return true if running.
   */
  public boolean evolve(Population beings, EvolveManager evolveManager)
  {
    int localGenerations = 0;
    ArrayList<Being> currentGeneration = new ArrayList<>();
    ArrayList<Being> nextGenerationHelper = new ArrayList<>();
    // copy all the beings to be evolved
    currentGeneration.addAll(beings.getBeings());
    // clear the beings from main pop
    beings.clear();

    do
    {
      localGenerations++;
      statistics.addGenerationToSum(1);
      nextGenerationHelper = helper(currentGeneration);


      for (Being individual : nextGenerationHelper)
      {
        // add the being (children) back into the population
        beings.add(individual);

        environment.beginEvaluation(individual);
        while (true)
        {
          if (!individual.isUnderEvaluation())
          {
            break;
          }
        }

        float fitness = individual.getFitness();
        statistics.addFitnessToSum((fitness));
        if (fitness >= statistics.getBestFitness())
        {
          statistics.setBestBeing(individual);
        }

        if (fitness < statistics.getAverageFitness())
        {
          // if the being is really bad and remove there is a 50% chance it'll
          // be removed
          if (random.nextBoolean())
          {
            beings.remove(individual);
          }
        }
      }

    }
    while (localGenerations < GENERATIONS_TO_CREATE);
    evolveManager.setMuting(true);
    return false;
  }



  private ArrayList<Being> helper(ArrayList<Being> beings)
  {
    Random rnd = new Random();
    Being parent1;
    Being parent2;
    nextGeneration = new ArrayList<>();

    ArrayList<Being> newParents = select.select(beings);



    //elitism: put one copy of unaltered most fit being and one mutated version of most fit being directly into nextGen
    //pick random pairs of parents from population pool
    nextGeneration.add(statistics.getBestBeing());

    statistics.getBestBeing().setAge(statistics.getBestBeing().getAge() + 1);

    Being mutatedBest = statistics.getBestBeing().clone();
    mutation(mutatedBest);
    nextGeneration.add(mutatedBest);

    mutatedBest.setAge(mutatedBest.getAge() + 1);

    //Pick pairs of parents randomly for breeding
    while (newParents.size() > 2)
    {
      statistics.addCrossesToSum(1);
      int parent1index = rnd.nextInt(newParents.size());
      parent1 = newParents.get(parent1index);

      newParents.remove(newParents.get(parent1index));

      int parent2index = rnd.nextInt(newParents.size());
      parent2 = newParents.get(parent2index);


      newParents.remove(newParents.get(parent2index));

      //Perform crossover on selected parents (pctCrossover percent of the time) and replace parents with children;
      //crossover produces 2 children per set of parents
      if (rnd.nextInt(100) < PERCENT_CROSSOVER)
      {
        ArrayList<Being> children;
        if (CROSSOVER == GA_TYPE.CROSSOVER)
        {
          children = ga.run(parent1, parent2);  // use crossover2 method for simpler, but fast-growing crossover

        }
        else
        {
          children = ga.run(parent1, parent2);  // use crossover2 method for simpler, but fast-growing crossover
        }
        parent1 = children.get(0);
        parent2 = children.get(1);

        parent1.setChildren(parent1.getChildren() + 1);
        parent2.setChildren(parent1.getChildren() + 1);

      }
      //If no crossover for this pair, these parents move on to the next generation
      else
      {
        parent1.setAge(parent1.getAge() + 1);
        parent2.setAge(parent1.getAge() + 1);

      }
      //Perform mutation operation on parent1 pctMutation percent of the time
      if (rnd.nextInt(100) < PERCENT_MUTATION)
      {
        mutation(parent1);
      }

      //Perform mutation operation on parent2 pctMutation percent of the time
      if (rnd.nextInt(100) < PERCENT_MUTATION)
      {
        mutation(parent2);
      }

      //add two resulting individuals to next generation
      nextGeneration.add(parent1);
      nextGeneration.add(parent2);
    }
    return nextGeneration;
  }


  private void mutation(Being individual)
  {
    Vector3f vector3f = new Vector3f();
    Random rand = new Random();
    float scaleFactor = rand.nextBoolean() ? 1.05f : 0.95f;
    // only mutate diminution
    individual.getGenotype().getRoot().getDimensions(vector3f);

    vector3f.x *= scaleFactor;
    vector3f.y *= scaleFactor;
    vector3f.z *= scaleFactor;

    if (Block.min(vector3f) < 0.5f)
    {
      return;
    }

    if (Block.max(vector3f) > 10 * Block.min(vector3f))
    {
      return;
    }

    individual.getGenotype().getRoot().setDimensions(vector3f);

    for (Gene neighbor : individual.getGenotype().neighbors(individual.getGenotype().getRoot()))
    {
      neighbor.getEffector().getParent(vector3f);
      vector3f.x *= scaleFactor;
      vector3f.y *= scaleFactor;
      vector3f.z *= scaleFactor;
      neighbor.getEffector().setParent(vector3f);
    }


  }

  public void setDataHandler(Statistics dataHandler)
  {
    this.statistics = dataHandler;
    select = new TournamentSelect(dataHandler);
  }
}



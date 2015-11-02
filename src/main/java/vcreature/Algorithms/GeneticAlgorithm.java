package vcreature.Algorithms;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import vcreature.*;
import vcreature.genotype.Effector;
import vcreature.genotype.Genome;
import vcreature.genotype.Gene;
import vcreature.phenotype.Block;
import vcreature.phenotype.Creature;
import vcreature.phenotype.PhysicsConstants;

import java.util.*;

/**
 * @author Cari
 */
public class GeneticAlgorithm
{


  private int totalGenerations = 1;
  private int populationSize;
  private int pctMutations = 0;
  private int pctCrossover = 90;
  private int generationNumber;
  private double bestFitness;
  private Being bestBeing;
  private Population initialPopulation;
  private Genome temp1 = new Genome(); //for re-use in each crossover operation
  private Genome temp2 = new Genome(); //for re-use in each crossover operation
  private GAMain simulation;
  private Timer timer = new Timer();



  public GeneticAlgorithm(Population initPop, GAMain sim)
  {
    this.simulation = sim;
    this.generationNumber = 0;
    this.bestFitness = 0;
    this.bestBeing = null;
    this.initialPopulation = initPop;
    this.populationSize = initPop.size();
  }

  public Population getInitialPopulation()
  {
    return initialPopulation;
  }

  //TODO: make real fitness function
  //test fitness function; maximize surface area of all blocks; this is to test that the GA works to solve an optimization problem
  protected float calcFitness(Being individual)
  {


    float surfaceArea = 0.0f;
    Genome genotype = individual.getGenotype();

 /*   timer.schedule(new TimerTask()
    {
      @Override
      public void run()
      {

      }
    }, 10000);

    try
    {
      simulation.getEnvironment().addToWorld(individual);
      Thread.sleep(1000);
    }
    catch(InterruptedException e)
    {
      Thread.currentThread().interrupt();
    }

*/
//    individual.setFitness(individual.getPhenotype().getFitness());
//    simulation.getEnvironment().addToWorld(individual);
//    simulation.getEnvironment().removeFromWorld();

    LinkedList<Gene> genes = genotype.getGenes();
    for(Gene gene : genes)
    {
      surfaceArea = surfaceArea +  2 * gene.getHeightY()*gene.getLengthX() + 2 * gene.getHeightY() * gene.getWidthZ() + 2 * gene.getLengthX() * gene.getWidthZ();
    }

    return surfaceArea;
  }

  protected void printFitnessStats(Vector<Being> beings)
  {
    float bestFitness = 0f;
    float avgFitness = 0f;
    float summedFitness = 0f;
    float currentBeingFitness;
    for(Being being : beings)
    {
      currentBeingFitness = being.getFitness();
      System.out.println("current being fitness: " + currentBeingFitness);
      if(currentBeingFitness > bestFitness)
      {

        bestFitness = being.getFitness();
      }
      summedFitness = summedFitness + currentBeingFitness;
    }
    avgFitness = summedFitness / beings.size();
    System.out.println(beings.size());
    System.out.println("Generation: " + generationNumber);
    System.out.println("Best fitness " + bestFitness);
    System.out.println("Avg fitness " + avgFitness);
   /* System.out.println("Best being");
    if(this.bestBeing != null)
    {
      printBeing(this.bestBeing);
    }
    */
  }

  protected void printBeing(Being being)
  {


    int geneNumber = 0;


    for(Gene gene : being.getGenotype().getGenes())
    {
      geneNumber++;
      System.out.println("Gene " + geneNumber);
      System.out.println("h " + gene.getHeightY() + " w " + gene.getWidthZ() + " l " + gene.getLengthX());
      System.out.println("fitness " + being.getFitness());

    }
  }

  protected Vector<Being> selection (Vector<Being> population)
  {


    //hack to generate random population
/*    for(Being being : population)
    {
      mutation(being);
      LinkedList<Gene> genes = being.getGenotype().getGenes();
    }
*/
    Vector<Being> newParents = new Vector();
    Random rnd = new Random();

    float currentGenBestFitness = 0f;
    Being currentGenBestBeing = population.get(0);

    //get fitness for every member of population
    for(Being being : population)
    {
      float fitness = calcFitness(being);
      being.setFitness(fitness);
      if(fitness > currentGenBestFitness)
      {
        currentGenBestBeing = being;
        currentGenBestFitness = fitness;
      }
    }

    if(this.bestBeing == null || currentGenBestBeing.getFitness() > this.bestBeing.getFitness())
    {
      this.bestBeing = currentGenBestBeing;
      this.bestFitness = currentGenBestBeing.getFitness();
    }
    //elitism;take the best individual unchanged into the next generation
    newParents.add(currentGenBestBeing);
    //take one random member of the population to meet spac that requires every member of population has chance of being selected
    newParents.add(population.get(rnd.nextInt(population.size())));

    //tournament selection; tournament size 2; take random pairs from population; compare fitness; take better Being into next generation
    //need to keep population size consistent; take elitism and randomly added beings into account
    for(int i = 0; i < populationSize - 2; i++)
    {
      int rndIndex1 = rnd.nextInt(population.size());
      int rndIndex2 = rnd.nextInt(population.size());

      Being being1 = population.get(rndIndex1);

      while(rndIndex2 == rndIndex1)
      {
        rndIndex2 = rnd.nextInt(population.size());
      }
      Being being2 = population.get(rndIndex2);

      if(being1.getFitness() >= being2.getFitness())
      {
        newParents.add(being1);
      }
      else
      {
        newParents.add(being2);
      }

    }

    return newParents;
  }

//
  protected void setParentJoint (Gene parent, Gene child)
  {

    Effector eChild = child.getEffector();
    Random rnd = new Random();
    float x = Math.signum(eChild.getParentX());
    float y = Math.signum(eChild.getParentY());
    float z = Math.signum(eChild.getParentZ());

    if(eChild.getPivotAxisX() == 1.0)
    {
      eChild.setParentX(x*(parent.getLengthX() - (rnd.nextFloat()*parent.getLengthX())));
      eChild.setParentY(y * parent.getHeightY() / 2f);
      eChild.setParentZ(z * parent.getWidthZ() / 2f);
    }
    else if(eChild.getPivotAxisY() == 1.0)
    {
      eChild.setParentY(y * (parent.getHeightY() - (rnd.nextFloat() * parent.getHeightY())));
      eChild.setParentX(x * parent.getLengthX() / 2f);
      eChild.setParentZ(z * parent.getWidthZ() / 2f);
    }
    else if(eChild.getPivotAxisZ() == 1.0)
    {
      eChild.setParentZ(z * (parent.getWidthZ() - (rnd.nextFloat() * parent.getWidthZ())));
      eChild.setParentY(y * parent.getHeightY() / 2f);
      eChild.setParentX(x * parent.getLengthX() / 2f);
    }

  }

  protected void printDimensions(Gene g)
  {
    System.out.println(" X " + g.getLengthX() + " Y " + g.getHeightY() + " Z " + g.getWidthZ());
  }

  private void printEffector(Effector e)
  {
    System.out.println(" ParentX " + e.getParentX() + " ParentY " + e.getParentY() + " ParentZ " + e.getParentZ());
    System.out.println(" ChildX " + e.getChildX() + " ChildY " + e.getChildY() + " ChildZ " + e.getChildZ());
    System.out.println(" PivotX " + e.getPivotAxisX() + " PivotY " + e.getPivotAxisY() + " PivotZ " + e.getPivotAxisZ());
  }

  protected int getParentIndex(Genome genotype, int geneIndex)
  {

    int parentIndex = 0;
    Gene possibleParent;
    for(int i = 0; i < genotype.getGenes().size(); i++)
    {
      possibleParent = genotype.getGenes().get(i);
      for(int j = 0; j < possibleParent.getEdges().size(); j++)
      {
        if(possibleParent.getEdges().get(j) == geneIndex)
        {
          return i;
        }
      }
    }
    return parentIndex;
  }


  protected void crossover(Being parent1, Being parent2)
  {

    if (parent1.getGenotype().getGenes().size() < 2 || parent2.getGenotype().getGenes().size() < 2)
    {
      System.out.println("CROSSOVER not performed; crossover not implemented for parent size 1");
      return;
    }


    Random rnd = new Random();
    //index of first gene to be swapped on each parent
    int crossoverPoint1 = 1 + rnd.nextInt((parent1.getGenotype().getGenes().size() - 1));
    int crossoverPoint2 = 1 + rnd.nextInt((parent2.getGenotype().getGenes().size() - 1));
    Genome genotype1 = parent1.getGenotype();
    ArrayList<Gene> genotype1COPY = new ArrayList();

    Gene gene1 = genotype1.getGenes().get(crossoverPoint1);
    Genome genotype2 = parent2.getGenotype();
    Gene gene2 = parent2.getGenotype().getGenes().get(crossoverPoint2);


    List<Gene> neighbors1 = new ArrayList();
    List<Gene> neighbors2 = new ArrayList();
    //   ArrayList<Integer> descendants = new ArrayList();


    neighbors1 = genotype1.neighbors(gene1);

    neighbors2 = genotype2.neighbors(gene2);
    int parentIndex1 = getParentIndex(genotype1, crossoverPoint1);
    int parentIndex2 = getParentIndex(genotype2, crossoverPoint2);

    //make copy of parent1 genes

    for (Gene g : genotype1.getGenes())
    {
      genotype1COPY.add(g.clone());
    }


    if (neighbors1.size() == 0 && neighbors2.size() == 0)
    {

      //swap genes at crossover points; update parent gene to remove this edge; add new edge from that parent to this new gene
      genotype1.getGenes().remove(crossoverPoint1);
      genotype1.getGenes().add(crossoverPoint1, gene2);
      setParentJoint(genotype1.getGenes().get(parentIndex1), gene2);

      genotype2.getGenes().remove(crossoverPoint2);
      genotype2.getGenes().add(crossoverPoint2, gene1);
      setParentJoint(genotype2.getGenes().get(parentIndex2), gene1);


    } else
    {

      ArrayList<Integer> genesToSwap1COPY = new ArrayList();
      ArrayList<Integer> genesToSwap2COPY = new ArrayList();
      ArrayList<Integer> genesToSwap1 = getDescendants(genotype1, crossoverPoint1, genesToSwap1COPY);
      ArrayList<Integer> genesToSwap2 = getDescendants(genotype2, crossoverPoint2, genesToSwap2COPY);

      if (genesToSwap2.size() <= genesToSwap1.size())
      {


        //indices of genes to swap
        ArrayList<Integer> test1 = new ArrayList();
        ArrayList<Integer> test2 = new ArrayList();


        System.out.println("TEST CROSSOVER MULTIPLE GENES");
        System.out.println("CROSSOVER POINT 1: " + crossoverPoint1);
        System.out.println(getDescendants(genotype1, crossoverPoint1, test1));
        System.out.println("PARENT1");
        for (int i = 0; i < genotype1.getGenes().size(); i++)
        {
          System.out.println("Gene" + i);
          printDimensions(genotype1.getGenes().get(i));
          System.out.println(genotype1.getGenes().get(i).getEdges());

        }
        System.out.println("CROSSOVER POINT 2: " + crossoverPoint2);
        System.out.println(getDescendants(genotype2, crossoverPoint2, test2));
        System.out.println("PARENT2");
        for (int i = 0; i < genotype2.getGenes().size(); i++)
        {
          System.out.println("Gene" + i);
          printDimensions(genotype2.getGenes().get(i));
          System.out.println(genotype2.getGenes().get(i).getEdges());

        }

        if (genesToSwap2.size() <= genesToSwap1.size())
        {
          System.out.println("**TESTING swap2 <= swap 1**");
          genotype1.getGenes().remove((int) genesToSwap1.get(0));
          genotype1.getGenes().add(genesToSwap1.get(0), genotype2.getGenes().get(genesToSwap2.get(0)));
          setParentJoint(genotype1.getGenes().get(parentIndex1), gene2);
          int j = 1;
          /*if (genesToSwap2.size() > 1)
          {
            for (int i = 1; i < genesToSwap2.size(); i++)
            {
              j++;
              System.out.println("i=" + i);
              genotype1.getGenes().remove((int) genesToSwap1.get(i));
              genotype1.getGenes().add(genesToSwap1.get(i), genotype2.getGenes().get(genesToSwap2.get(i)));

            }
          }
          */

          for (int k = genesToSwap1.size() - 1; k >= j; k--)
          {
            int parentIdx = getParentIndex(genotype1, ((int)genesToSwap1.get(k)));
            genotype1.getGenes().get(parentIdx).removeEdge(genesToSwap1.get(k));
            genotype1.getGenes().remove((int) genesToSwap1.get(k));

          }

          HashMap<Integer, ArrayList<Integer>> updateEdges = new HashMap();
          HashMap<Integer, ArrayList<Integer>> removeEdges = new HashMap();
          for (int m = 0; m < genotype1.getGenes().size(); m++)
          {
            ArrayList<Integer> newEdges = new ArrayList();
            ArrayList<Integer> oldEdges = new ArrayList();
            for (int edge : genotype1.getGenes().get(m).getEdges())
            {
              int count = 0;

              for (int i = 1; i < genesToSwap1.size(); i++)
              {
                if (edge > genesToSwap1.get(i))
                {
                  count++;
                }
              }

              oldEdges.add(edge);
              newEdges.add(edge - count);
            }
            removeEdges.put(m, oldEdges);
            updateEdges.put(m, newEdges);

          }

          for (int m = 0; m < updateEdges.size(); m++)
          {
            for (int n : updateEdges.get(m))
            {
              genotype1.getGenes().get(m).addEdge(n);
            }
          }

          for (int m = 0; m < removeEdges.size(); m++)
          {
            for (int n : removeEdges.get(m))
            {
              genotype1.getGenes().get(m).removeEdge(n);
            }
          }

/*          if(genesToSwap2.size() > 1)
          {
            int parent = crossoverPoint1;
            HashMap<Integer, Integer> addedVerticesMap = new HashMap(); //map index of genesToSwap to new index
            addedVerticesMap.put(genesToSwap2.get(0), crossoverPoint1);
            for(int i = 1; i < genesToSwap2.size(); i++)
            {
              System.out.println("Gene to swap " + genesToSwap2.get(i) + " parentidx " + getParentIndex(genotype2, (genesToSwap2.get(i))));
              System.out.println("MAP " + addedVerticesMap);
              parent = addedVerticesMap.get(getParentIndex(genotype2, (int)(genesToSwap2.get(i))));
              genotype1.getGenes().add(genotype2.getGenes().get(genesToSwap2.get(i)));
              genotype1.getGenes().get(parent).addEdge(genotype1.getGenes().size() - 1);
              addedVerticesMap.put(genesToSwap2.get(i), genotype1.getGenes().size() - 1);


            }

          }
*/
        }
        System.out.println("PARENT1 AFTER");
        for (int i = 0; i < genotype1.getGenes().size(); i++)
        {
          System.out.println("Gene" + i);
          printDimensions(genotype1.getGenes().get(i));
          System.out.println(genotype1.getGenes().get(i).getEdges());

        }
      }
    }


    LinkedList test = new LinkedList();
    for (int i = 0; i < genotype1.getGenes().size(); i++)
    {
      for (int edge : genotype1.getGenes().get(i).getEdges())
      {
        test.add(edge);
      }

    }
    System.out.println("EDGES AFTER CROSSOVER " + test);


  }


  private ArrayList<Integer> getDescendants(Genome genotype, int geneIndex, ArrayList<Integer> descendants)
  {

    descendants.add(geneIndex);
    ArrayList<Integer> neighbors = genotype.getGenes().get(geneIndex).getEdges();
    if(neighbors.size() == 0)
    {
      return descendants;
    }
    for (int neighbor : neighbors)
    {
      getDescendants(genotype, neighbor, descendants);
    }
    return descendants;
  }


  protected void mutation(Being individual)
  {
    Genome genotype = individual.getGenotype();
    LinkedList<Gene> genes = genotype.getGenes();
    Random rnd = new Random();
    Gene mutatedGene = genes.get(rnd.nextInt(genes.size()));

    int x = rnd.nextInt(3);
    switch(x)
    {
      case 0:
        if(0.5f + mutatedGene.getHeightY() <=10.0f)
        {
          mutatedGene.setHeightY(0.5f + mutatedGene.getHeightY());
        }
        break;
      case 1:
        if(0.5f + mutatedGene.getLengthX() <=10.0f)
        {
          mutatedGene.setLengthX(0.5f + mutatedGene.getLengthX());
        }
        break;
      case 2:
        if(0.5f + mutatedGene.getWidthZ() <=10.0f)
        {
          mutatedGene.setWidthZ(0.5f + mutatedGene.getWidthZ());
        }
        break;
    }

  }


  protected Vector<Being> createNextGeneration(Vector<Being>population)
  {
    Vector<Being> nextGeneration = new Vector();
    Vector<Being> newParents = selection(population);
    Random rnd = new Random();
    Being parent1;
    Being parent2;



    //elitism: put one copy of unaltered most fit being and one mutated version of most fit being directly into nextGen
    //pick random pairs of parents from population pool
    nextGeneration.add(this.bestBeing);
    nextGeneration.add(this.bestBeing);

    while(newParents.size() > 2)
    {
      int parent1index = rnd.nextInt(newParents.size());

      parent1 = newParents.get(rnd.nextInt(newParents.size()));
      newParents.remove(parent1index);
      int parent2index = rnd.nextInt(newParents.size());
      parent2 = newParents.get(rnd.nextInt(newParents.size()));
      newParents.remove(parent2index);
      if(rnd.nextInt(100) < this.pctCrossover)
      {
        crossover(parent1, parent2);
      }
      if (rnd.nextInt(100) < this.pctMutations)
      {
        mutation(parent1);
      }
      if (rnd.nextInt(100) < this.pctMutations)
      {
        mutation(parent2);
      }
      nextGeneration.add(parent1);
      nextGeneration.add(parent2);
    }


    return nextGeneration;
  }

  public Population evolvePopulation()
  {

    double currentGenBestFitness; //best fitness from current generation
    Being genBestBeing; ///most fit creature from current generation
    Vector<Being> currentGeneration= this.initialPopulation.getBeings();
    Vector<Being> nextGeneration = new Vector();
    double summedFitness;
    double averageFitness;

    do {
      summedFitness = 0;
      averageFitness = 0;
      currentGenBestFitness = 0;
      genBestBeing = currentGeneration.get(0);
      nextGeneration = createNextGeneration(currentGeneration);
      this.generationNumber++;
      for (Being individual : nextGeneration) {
        double fitness = calcFitness(individual);
        summedFitness = summedFitness + fitness;
        if (fitness > currentGenBestFitness) {
          currentGenBestFitness = fitness;
          genBestBeing = individual;
        }
        if (fitness > this.bestFitness) {
          this.bestFitness = fitness;
          this.bestBeing = individual;
        }
      }
      averageFitness = summedFitness / nextGeneration.size();
      printFitnessStats(nextGeneration);

      currentGeneration = nextGeneration;



    }
    while (generationNumber < this.totalGenerations);
    Population finalPop = new Population(nextGeneration, this.simulation.getEnvironment());
    finalPop.setGenerations(this.totalGenerations);
    finalPop.setAverageFitness((float) averageFitness);
    finalPop.setBestFitness((float)this.bestFitness);

    return finalPop;
  }

}



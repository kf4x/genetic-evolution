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
import vcreature.phenotype.PhysicsConstants;

import java.util.*;

/**
 * @author Cari
 */
public class GeneticAlgorithm
{


  private int totalGenerations;
  private int populationSize;
  private int pctMutations = 50;
  private int pctCrossover = 90;
  private int generationNumber;
  private double bestFitness;
  private Being bestBeing;
  private Population initialPopulation;
  private Genome temp1 = new Genome(); //for re-use in each crossover operation
  private Genome temp2 = new Genome(); //for re-use in each crossover operation



  GeneticAlgorithm(Population initPop)
  {
    this.populationSize = 200;
    this.pctCrossover = 90;
    this.pctMutations = 50;
    this.generationNumber = 0;
    this.bestFitness = 0;
    this.bestBeing = null;
    this.initialPopulation = initPop;
    this.totalGenerations = 200;



  }

  public Population getInitialPopulation()
  {
    return initialPopulation;
  }

  //TODO: make real fitness function
  //test fitness function; maximize surface area of all blocks; this is to test that the GA works to solve an optimization problem
  protected float calcFitness(Being individual)
  {
    float fitness = 0.0f;
    float surfaceArea = 0.0f;
    Genome genotype = individual.getGenotype();
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
      System.out.println(currentBeingFitness);
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
    Being currentGenBestBeing = null;

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


  protected void setParentJoint (Gene parent, Gene child)
  {
    Effector eChild = child.getEffector();
    System.out.println("CHILD: ");
    printEffector(eChild);
    Effector eParent = parent.getEffector();
    System.out.println("PARENT: ");
    printEffector(eParent);
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

    if(parent1.getGenotype().getGenes().size() < 2 || parent2.getGenotype().getGenes().size() < 2)
    {
      System.out.println("CROSSOVER not performed; crossover not implemented for parent size 1");
      return;
    }

/*    temp1.getGenes().addFirst(parent1.getGenotype().getGenes().get(0));
    temp2.getGenes().addFirst(parent2.getGenotype().getGenes().get(0));
    for(int i = 1; i < parent1.getGenotype().getGenes().size(); i++)
    {
      temp1.getGenes().add(parent1.getGenotype().getGenes().get(i));
    }
    for(int i = 1; i < parent2.getGenotype().getGenes().size(); i++)
    {
      temp2.getGenes().add(parent2.getGenotype().getGenes().get(i));
    }

*/

    Random rnd = new Random();
    //index of first gene to be swapped on each parent
    int crossoverPoint1 = 1 + rnd.nextInt((parent1.getGenotype().getGenes().size() - 1));
 //   System.out.println("Crossover point 1 =" + crossoverPoint1);
    int crossoverPoint2 = 1 + rnd.nextInt((parent2.getGenotype().getGenes().size() - 1));
 //   System.out.println("Crossover point 2 =" + crossoverPoint2);
    Genome currentGenotype = parent1.getGenotype();
    Gene currentGene = currentGenotype.getGenes().get(crossoverPoint1);
    Genome currentGenotype2 = parent2.getGenotype();
    Gene currentGene2 =  parent2.getGenotype().getGenes().get(crossoverPoint2);

    List<Gene> neighbors;

    neighbors = currentGenotype.neighbors(currentGene);


    if(neighbors.size() == 0 && currentGenotype2.neighbors(currentGene2).size() == 0)
    {
    //  System.out.println("PARENT 1 BEFORE");
    //  printBeing(parent1);
    //  System.out.println("PARENT 2 BEFORE");
    //  printBeing(parent2);

      //swap genes at crossover points; update parent gene to remove this edge; add new edge from that parent to this new gene
      currentGenotype.getGenes().remove(currentGene);
      currentGenotype.getGenes().get(getParentIndex(currentGenotype, crossoverPoint1)).removeEdge((crossoverPoint1));
      currentGenotype.append(currentGene2);
      currentGenotype.linkGenes((getParentIndex(currentGenotype, crossoverPoint1)), currentGenotype.size() - 1);

      currentGenotype2.getGenes().remove(currentGene2);
      currentGenotype2.append(currentGene);
      currentGenotype2.getGenes().get(getParentIndex(currentGenotype2, crossoverPoint2)).removeEdge((crossoverPoint2));
      currentGenotype2.linkGenes((getParentIndex(currentGenotype2, crossoverPoint2)), currentGenotype2.size() - 1);



     // System.out.println("PARENT 1  AFTER");
     // printBeing(parent1);
     // System.out.println("PARENT 2  AFTER");
     // printBeing(parent2);
    }
    for(Gene neighbor : neighbors)
    {
     // System.out.println(neighbor.getLengthX() + " " + neighbor.getHeightY() + " " + neighbor.getWidthZ());
    }
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

  protected Vector<Being> evolvePopulation()
  {

    double currentGenBestFitness; //best fitness from current generation
    Being genBestBeing; ///most fit creature from current generation
    Vector<Being> currentGeneration= this.initialPopulation.getBeings();
    Vector<Being> nextGeneration = new Vector();

    do {
      double summedFitness = 0;
      double averageFitness = 0;
      currentGenBestFitness = 0;
      genBestBeing = null;
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
      printFitnessStats(nextGeneration);

      currentGeneration = nextGeneration;

      averageFitness = summedFitness / nextGeneration.size();

    }
    while (generationNumber < this.totalGenerations);
    return nextGeneration;
  }

}



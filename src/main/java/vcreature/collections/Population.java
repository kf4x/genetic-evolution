package vcreature.collections;

/**
 * @author Javier Chavez
 * @author Alex Baker
 * @author Dominic Salas
 * @author Carrie Martinez
 * <p>
 * Date November 4, 2015
 * CS 351
 * Genetic Evolution
 */


import vcreature.Being;
import vcreature.morphology.GeneticAlgorithm;
import vcreature.morphology.HillClimb;
import vcreature.utils.Savable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Class that handles the entire population... at every update
 * some number of beings are chosen and sent to the breeder to be
 * evolved
 */
public class Population extends ArrayList<Being> implements Savable
{
  private final ArrayList<Being> beings;

  private volatile int generations = 0;
  private volatile float averageFitness = 1;
  private volatile float bestFitness = 1;
  private volatile float totalLifetimeFitness = 1;
  private volatile Being bestBeing = null;

  private GeneticAlgorithm breeding;
  private HillClimb mutating;

  private volatile long lifetimeOffspring;
  private volatile long lifetimeHillClimbs;
  private volatile long currentRejectedCreatures;
  private volatile long currentFailedHillClimbs;
  private volatile long lifetimeRejectedCreatures;
  private volatile long lifetimeFailedHillClimbs;
  private float pastAverageFitness;
  // private boolean isEvolving = false;


  public Being getBestBeing()
  {
    return bestBeing;
  }

  public void setBestBeing(Being bestBeing)
  {
    this.bestBeing = bestBeing;
  }

  public float getTotalLifetimeFitness()
  {
    return totalLifetimeFitness;
  }

  public void setTotalLifetimeFitness(float totalLifetimeFitness)
  {
    this.totalLifetimeFitness = totalLifetimeFitness;
  }

  public float getAverageFitness()
  {
    return averageFitness;
  }

  public float getBestFitness()
  {
    return bestFitness;
  }

  public void setBreeding(GeneticAlgorithm breeding)
  {
    this.breeding = breeding;
  }

  public void setMutating(HillClimb mutating)
  {
    this.mutating = mutating;
  }

  public long getLifetimeOffspring()
  {
    return lifetimeOffspring;
  }

  public void setLifetimeOffspring(long lifetimeOffspring)
  {
    this.lifetimeOffspring = lifetimeOffspring;
  }

  public long getLifetimeHillClimbs()
  {
    return lifetimeHillClimbs;
  }

  public void setLifetimeHillClimbs(long lifetimeHillClimbs)
  {
    this.lifetimeHillClimbs = lifetimeHillClimbs;
  }

  public long getCurrentRejectedCreatures()
  {
    return currentRejectedCreatures;
  }

  public void setCurrentRejectedCreatures(long currentRejectedCreatures)
  {
    this.currentRejectedCreatures = currentRejectedCreatures;
  }

  public long getCurrentFailedHillClimbs()
  {
    return currentFailedHillClimbs;
  }

  public void setCurrentFailedHillClimbs(long currentFailedHillClimbs)
  {
    this.currentFailedHillClimbs = currentFailedHillClimbs;
  }

  public long getLifetimeRejectedCreatures()
  {
    return lifetimeRejectedCreatures;
  }

  public void setLifetimeRejectedCreatures(long lifetimeRejectedCreatures)
  {
    this.lifetimeRejectedCreatures = lifetimeRejectedCreatures;
  }

  public long getLifetimeFailedHillClimbs()
  {
    return lifetimeFailedHillClimbs;
  }

  public void setLifetimeFailedHillClimbs(long lifetimeFailedHillClimbs)
  {
    this.lifetimeFailedHillClimbs = lifetimeFailedHillClimbs;
  }

  /**
   * Creates a population from the given vector of beings. Also takes algorithms
   * for mutating and breeding
   *
   * @param beings A vector of beings to init the population too
   * @param breeding A genetic algorithm which spcifies how beings are bred
   * @param mutating A hill cliimbing algorithm to mutate the beings
   */
  public Population(ArrayList<Being> beings, GeneticAlgorithm breeding, HillClimb mutating)
  {
    this.beings = beings;
    this.breeding = breeding;
    this.mutating = mutating;
  }

  /**
   * Creates a population with algorithms
   * for mutating and breeding the population
   *
   * @param breeding A genetic algorithm which spcifies how beings are bred
   * @param mutating A hill cliimbing algorithm to mutate the beings
   */
  public Population(GeneticAlgorithm breeding, HillClimb mutating)
  {
    this(new ArrayList<>(200), breeding, mutating);
  }

  /**
   * Get the beings in the population
   *
   * @return a vector of beings
   */
  public ArrayList<Being> getBeings()
  {
    return beings;
  }

  /**
   * Set the generations for the population
   *
   * @param generations number of generations to apply to the population
   */
  public void setGenerations(int generations)
  {
    this.generations = generations;
  }

  /**
   * Set the average fitness of the population
   *
   * @param averageFitness average fitness to apply to the population
   */
  public void setAverageFitness(float averageFitness)
  {
    this.averageFitness = averageFitness;
  }

  /**
   * Set the best fitness for the population
   *
   * @param bestFitness the best fitness to apply to the population
   */
  public void setBestFitness(float bestFitness)
  {
    this.bestFitness = bestFitness;
  }

  /**
   * This is called by the subpopulation threads. I have a check(isEvovling) just
   * in case we want to use it.
   */
  public void update() {
    generations++;
    /*
      OR maybe this is where the population gets switched.

    if (!isEvolving)
    {
      isEvolving = true;

      for (Being being : beings)
      {
        logger.export(being.getGenotype());
      }

      breeding.evolvePopulation(beings, this);
    }
    */
  }

  /**
   * Get the breeding used in the population
   *
   * @return the breeding (genetic) algorithm
   */
  public GeneticAlgorithm getBreeding()
  {
    return breeding;
  }

  /**
   * Get the mutation algorithm in the population
   *
   * @return the mutating (hill climbing) algorithm
   */
  public HillClimb getMutating()
  {
    return mutating;
  }

  /**
   * Get the number of generations in the population
   *
   * @return number of generations
   */
  public int getGenerations()
  {
    return generations;
  }

  /**
   * Replace a being at index i with a new being
   *
   * @param i index to replace a being
   * @param being new being to insert at index i
   */
  public synchronized void replace(int i, Being being)
  {
    this.beings.remove(i);
    this.beings.add(i, being);
  }

  @Override
  public synchronized List<Being> subList(int fromIndex, int toIndex)
  {
    return beings.subList(fromIndex, toIndex);
  }

  @Override
  public synchronized Being remove(int index)
  {
    return beings.remove(index);
  }

  @Override
  public synchronized Being get(int index)
  {
    return beings.get(index);
  }

  @Override
  public synchronized int size()
  {
    return beings.size();
  }

  @Override
  public synchronized boolean add(Being being)
  {
    return this.beings.add(being);
  }

  @Override
  public void write(StringBuilder s)
  {
    s.append(LocalDateTime.now()).append(",");
    s.append(averageFitness).append(",");
    s.append(bestFitness).append(",");
    s.append(this.size());
  }

  @Override
  public void read(StringBuilder s)
  {

  }

  public float getPastAverageFitness()
  {
    return pastAverageFitness;
  }


  public void setPastAverageFitness(float pastAverageFitness)
  {
    this.pastAverageFitness = pastAverageFitness;
  }
}
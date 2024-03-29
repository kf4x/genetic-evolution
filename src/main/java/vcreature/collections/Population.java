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
import vcreature.genotype.Gene;
import vcreature.morphology.GeneticAlgorithm;
import vcreature.morphology.HillClimb;
import vcreature.utils.Savable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Class that handles the entire population... at every update
 * some number of beings are chosen and sent to the breeder to be
 * evolved
 */
public class Population extends ArrayList<Being> implements Savable
{
  private final ArrayList<Being> beings;
  private GeneticAlgorithm breeding;
  private HillClimb mutating;


  /**
   * Creates a population from the given vector of beings. Also takes algorithms
   * for mutating and breeding
   *
   * @param beings   A vector of beings to init the population too
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
   * Replace a being at index i with a new being
   *
   * @param i     index to replace a being
   * @param being new being to insert at index i
   */
  public synchronized void replace(int i, Being being)
  {
    remove(i);
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
    int i = beings.get(index).getGenotype().size();
    Gene.TOTAL -= i;
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
    Gene.TOTAL += being.getGenotype().size();
    return this.beings.add(being);
  }

  @Override
  public void write(StringBuilder s)
  {
    for (Being being : beings)
    {
      being.write(s);
    }

  }

  @Override
  public void read(StringBuilder s)
  {
  }

  public Being getActive()
  {

    for (Being being1 : beings)
    {
      if (being1.isUnderEvaluation())
      {
        return being1;
      }
    }
    return null;
  }

  public Being getBest()
  {
    Collections.sort(beings);
    return beings.get(0);
  }


  @Override
  public void clear()
  {
    beings.clear();
    Gene.TOTAL = 0;
  }
}

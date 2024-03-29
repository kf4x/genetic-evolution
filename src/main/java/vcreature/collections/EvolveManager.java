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


import vcreature.utils.Savable;
import vcreature.utils.Statistics;

import java.util.Collections;

import static vcreature.morphology.GeneticAlgorithmParams.GA;
import static vcreature.morphology.GeneticAlgorithmParams.HILL_CLIMB;


/**
 * This is the wrapper for actually splitting the large population up
 * into smaller ones so that threads can handle scaling.
 */
public class EvolveManager extends Thread implements Savable
{
  private Population population;
  private volatile boolean paused = true;
  private boolean running = true;
  private boolean isEvolving = false;
  private Boolean mutate = true;
  private Statistics statistics;

  /**
   * Create a evolution given a population
   *
   * @param population population that will be split into SubPopulations
   */
  public EvolveManager(Population population, Statistics statistics)
  {
    this.population = population;
    this.statistics = statistics;
  }


  public boolean isEvolving()
  {
    return isEvolving;
  }

  /**
   * Start the next generation in the sub population
   */
  public void cross()
  {
    synchronized (this)
    {
      if (!isEvolving)
      {
        isEvolving = true;

        if (GA || HILL_CLIMB)
        {
          if (mutate && HILL_CLIMB)
          {

            System.out.println("[BEGIN]: HC");
            statistics.setProcess("HC");
            population.getMutating().evolve(population, this);
            interrupt();
            isEvolving = false;
            System.out.println("[END]: HC");

          }
          else
          {
            if (GA)
            {
              statistics.setProcess("GA");
              System.out.println("[START]: CROSS");
              population.getBreeding().evolve(population, this);
              interrupt();
              isEvolving = false;
              System.out.println("[END]: CROSS");
            }
            else
            {
              mutate = true;
              isEvolving = false;
              interrupt();
            }
          }
        }

        Collections.sort(population);

        if (population.size() > 300)
        {
          System.out.println("[TRIM]:  population.");
          for (int i = 0; i < population.size(); i++)
          {
            if (population.get(i).getFitness() < .10f)
            {
              population.remove(i);
            }
          }
        }

      }
    }
  }

  @Override
  public void run()
  {
    while (running)
    {
      synchronized (this)
      {
        if (Thread.interrupted())
        {
          paused = !paused;
        }

        if (!paused)
        {
          cross();
        }
      }
    }
  }


  /**
   * Get the current population
   *
   * @return the current population in the evolution
   */
  public Population getPopulation()
  {
    return population;
  }


  @Override
  public void write(StringBuilder s)
  {
  }

  @Override
  public void read(StringBuilder s)
  {

  }

  public void setMuting(boolean mutate)
  {
    this.mutate = mutate;
  }
}

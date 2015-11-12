package vcreature.utils;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import vcreature.Being;
import vcreature.collections.Population;
import vcreature.genotype.Gene;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Statistics implements Savable
{
  private volatile Population population;
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");
  private Logger statsLogger = new Logger("population-"+formatter.format(LocalDateTime.now())+".txt");

  float fitnessCurrentEvolingBeing=0;
  private volatile double  fitnessSumTotal=0;
  private volatile Being bestBeing=null;
  private volatile float currentGenBestFitness = 1;
  private volatile int populationSize=0;
  private volatile int generationNumber=1;
  private volatile float bestFitness=0;
  private volatile long lifetimeOffspring=0;
  private volatile long lifetimeHillClimbs=0;
  private volatile long currentRejectedCreatures=0;
  private volatile long currentFailedHillClimbs=0;
  private volatile long lifetimeRejectedCreatures =0;
  private volatile long lifetimeFailedHillClimbs =0;
  private volatile int generations = 0;
  private volatile float averageFitness = 0;
  private volatile long elapsedTime = 0L;
  private volatile double sumfitness = 0.0;
  private volatile int lifetimeCrosses = 0;
  private volatile double _past = 0;
  private volatile double _current = 0;
  private volatile float tenMinCounter = 0;
  private volatile float minCounter = 0;

  public Statistics(Population population)
  {
    this.population = population;
  }


  public float getFirstGenAvgFitness()
  {
    return firstGenAvgFitness;
  }

  public void setFirstGenAvgFitness(float firstGenAvgFitness)
  {
    this.firstGenAvgFitness = firstGenAvgFitness;
  }

//  public float setAverageFitness()
//  {
//    float d = 0f;
//    for (Being o : population)
//    {
//      d += o.getFitness();
//    }
//    averageFitness = d/population.size();
//    return averageFitness;
//  }

//  public void setCurrentGenAverageFitness(float currentGenAverageFitness)
//  {
//    this.currentGenAverageFitness = currentGenAverageFitness;
//  }

  public Being getBestBeing()
  {
    return bestBeing;
  }

  public void setBestBeing(Being bestBeing)
  {
    this.bestBeing = bestBeing;
    this.setBestFitness(bestBeing.getFitness());
  }

  public void addFitnessToSum(float fitness)
  {
    fitnessSumTotal += fitness;
  }

  public void addHillClimbToSum(int hc)
  {
    lifetimeHillClimbs += hc;
  }

  public void addCrossesToSum(int cc)
  {
    lifetimeCrosses+= cc;
  }

  public void addGenerationToSum(int g)
  {
    generationNumber+= g;
  }

  public float getBestFitness()
  {
    return bestFitness;
  }

  private void setBestFitness(float bestFitness)
  {
    this.bestFitness = bestFitness;
  }

  public double getAverageFitness()
  {
        float d = 0f;
        for (Being o : population)
        {
          d += o.getFitness();
        }
        averageFitness = d/population.size();
        return averageFitness;
  }


  public int getGenerationNumber()
  {
    return generationNumber;
  }

  public void setGenerationNumber(int generationNumber)
  {
    this.generationNumber = generationNumber;
  }

  public int getPopulationSize()
  {
    return population.size();
  }


  public double getFitnessSumTotal()
  {
    return fitnessSumTotal;
  }


  private float firstGenAvgFitness;

  public void update(float delta)
  {
    elapsedTime += delta;
    tenMinCounter += delta;
    minCounter += delta;

    if (minCounter >= 60)
    {
      if (_past == 0)
      {
        _past = getBestFitness();
      }
      _current = getBestFitness();
      _past = (_past + _current)/2;
    }
    if (tenMinCounter >= 60)
    {
      statsLogger.export(this);
      tenMinCounter = 0;
    }
  }

  public double getAverageFitnessMin()
  {
    return _past;
  }

  @Override
  public void write(StringBuilder s)
  {
    s.append("-------- Generation "+ getGenerationNumber() +" ---------\n");
    s.append("Time (elapsed min):\t" + ((float)elapsedTime/60.0f/60.0f)).append("\n");
    s.append("Population:\t" + populationSize).append("\n");
    s.append("Genes:\t" + Gene.TOTAL).append("\n");
    s.append("Beings:\t" + Being.TOTAL).append("\n");
    s.append("Average fitness:\t" + getAverageFitness()).append("\n");
    s.append("Best fitness:\t" + getBestFitness()).append("\n");
    s.append("Lifetime HillClimbs:\t" + lifetimeHillClimbs).append("\n");
    s.append("Lifetime Crossovers:\t" + lifetimeCrosses).append("\n");
    s.append("Average fitness/min:\t" + _past).append("\n");
    s.append("Diversity:\t" + (Being.TOTAL*getGenerationNumber())/Gene.TOTAL ).append("\n\n");
  }

  @Override
  public void read(StringBuilder s)
  {
    throw new NotImplementedException();
  }
}
package vcreature;


import java.util.*;

/**
 * Vector because of synchronization
 */
public class Population extends Vector<Being>
{
  private final Vector<Being> beings;

  private volatile int generations;
  private volatile float averageFitness;
  private volatile float bestFitness;
  private volatile long lifetimeOffspring;
  private volatile long lifetimeHillClimbs;
  private volatile long currentRejectedCreatures;
  private volatile long currentFailedHillClimbs;
  private volatile long lifetimeRejectedCreatures;
  private volatile long lifetimeFailedHillClimbs;
  private volatile Environment environment;

  public Population(Vector<Being> beings, Environment environment)
  {
    this.beings = beings;
    this.environment = environment;
  }

  public Population(Environment environment)
  {
    this(new Vector<>(2001), environment);
  }

  public void initPop()
  {
    for (int i = 0; i < 200; i++)
    {
      Being a = new Being();
       a.setPhenotype(FlappyBird.class, environment);
      beings.add(i, a);
    }
  }

  public void update() {
    generations++;
  }

  @Override
  public synchronized List<Being> subList(int fromIndex, int toIndex)
  {
    return beings.subList(fromIndex, toIndex);
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

  public int getGenerations()
  {
    return generations;
  }
}

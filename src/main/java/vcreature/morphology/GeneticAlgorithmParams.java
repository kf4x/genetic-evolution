package vcreature.morphology;


import static vcreature.morphology.GeneticStrategy.GA_TYPE;

public final class GeneticAlgorithmParams
{

  public static final int SELECTION = 3; // selection 2 or 3
  public static final GA_TYPE CROSSOVER = GA_TYPE.CROSSOVER_MUTATION;
  public static final float PERCENT_MUTATION = .90f;
  public static final float PERCENT_CROSSOVER = .90f;
  public static final int GENERATIONS_TO_CREATE = 50;


  public static final boolean HILL_CLIMB = true;
  public static final boolean GA = true;

}

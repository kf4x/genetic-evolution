package vcreature.genotype;

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


/**
 * This class represent the EnumNeuronInput TIME in the genotype
 */
public class TimeInput implements NeuralInput<Float>, Savable
{
  private float time;

  @Override
  public Float getValue()
  {
    return time;
  }

  @Override
  public TimeInput setValue(Float value)
  {
    this.time = value;
    return this;
  }

  @Override
  public void write(StringBuilder s)
  {
    s.append("TIME");
    s.append("[]").append(":");
    s.append(getValue()).append(",");
  }

  @Override
  public void read(StringBuilder s)
  {
    // setValue(Float.parseFloat(s.toString()));
  }
}

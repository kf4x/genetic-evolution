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


/**
 * This class represents the EnumNeuronInput JOINT in the genotype
 */
public class AngleSensor extends Sensor<AngleSensor, Float>
{
  /**
   * Senors shall only be instantiated by Genes
   */
  protected AngleSensor(Gene source)
  {
    super(source);
  }

  @Override
  public void write(StringBuilder s)
  {
    s.append("ANGLE");
    s.append("["+getSource().getPosition()+"]").append(":");
    s.append(getValue()).append(",");
  }

  @Override
  public void read(StringBuilder s)
  {
    //setValue(Float.parseFloat(s.toString()));
  }
}

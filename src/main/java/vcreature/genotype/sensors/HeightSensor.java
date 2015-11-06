package vcreature.genotype.sensors;

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


import vcreature.genotype.Gene;

/**
 * Sensor for Height. Height is measure from the center of the block to the ground
 */
public class HeightSensor extends Sensor<HeightSensor, Float>
{
  /**
   * Senors shall only be instantiated by Genes
   */
  protected HeightSensor(Gene source)
  {
    super(source);
  }

  @Override
  public void write(StringBuilder s)
  {
    s.append("HEIGHT").append(":");
    s.append(getValue()).append(",");
  }

  @Override
  public void read(StringBuilder s)
  {
    String value = s.substring(0, s.indexOf(","));
    setValue(Float.parseFloat(value));
    s.delete(0, value.length()+2);
  }
}
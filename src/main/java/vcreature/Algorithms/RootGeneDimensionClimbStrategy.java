package vcreature.Algorithms;

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


import com.jme3.math.Vector3f;
import vcreature.genotype.Gene;
import vcreature.genotype.Genome;
import vcreature.phenotype.Block;

import java.util.Random;


/**
 * This class is a mutation to change the volume of the root node of a computer
 *
 * @param <V> type of gene to mutate
 */
public class RootGeneDimensionClimbStrategy<V> implements HillClimbStrategy<Genome, V>
{
  Vector3f vector3f = new Vector3f();
  @Override
  public V climb(Genome part)
  {
    Random rand = new Random();
    float scaleFactor = rand.nextBoolean() ? 1.05f : 0.95f;
    // only mutate diminution
    part.getRoot().getDimensions(vector3f);

    vector3f.x *= scaleFactor;
    vector3f.y *= scaleFactor;
    vector3f.z *= scaleFactor;

    if (Block.min(vector3f) < 0.5f)
    {
      return null;
    }

    if (Block.max(vector3f) > 10*Block.min(vector3f))
    {
      return null;
    }

    part.getRoot().setDimensions(vector3f);

    for (Gene neighbor : part.neighbors(part.getRoot()))
    {
      neighbor.getEffector().getParent(vector3f);
      vector3f.x *= scaleFactor;
      vector3f.y *= scaleFactor;
      vector3f.z *= scaleFactor;
      neighbor.getEffector().setParent(vector3f);
    }
    return null;
  }
}

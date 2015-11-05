package vcreature.Algorithms;

import com.jme3.math.Vector3f;
import vcreature.genotype.Gene;
import vcreature.genotype.Genome;
import vcreature.phenotype.Block;

import java.util.Random;


public class LimbGeneDimensionClimbStrategy<V> implements HillClimbStrategy<Genome, V>
{
  Vector3f vector3f = new Vector3f();

  @Override
  public V climb(Genome part)
  {
    Random rand = new Random();
    float scaleFactor = rand.nextBoolean() ? 1.05f : 0.95f;
    // only mutate diminution

    for (Integer integer : part.getRoot().getEdges())
    {
      Gene g = part.get(integer);

      g.getEffector().getParent(vector3f);

      vector3f.x *= scaleFactor;
      vector3f.y *= scaleFactor;
      vector3f.z *= scaleFactor;

      if (Block.min(vector3f) < 0.5f)
      {
        continue;
      }

      if (Block.max(vector3f) > 10 * Block.min(vector3f))
      {
        continue;
      }

      g.getEffector().setParent(vector3f);


      g.getEffector().getChild(vector3f);
      vector3f.x *= scaleFactor;
      vector3f.y *= scaleFactor;
      vector3f.z *= scaleFactor;

      if (Block.min(vector3f) < 0.5f)
      {
        continue;
      }

      if (Block.max(vector3f) > 10 * Block.min(vector3f))
      {
        continue;
      }

      g.getEffector().setChild(vector3f);

    }
    return null;
  }


}

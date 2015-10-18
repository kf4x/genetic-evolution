package vcreature;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.bullet.PhysicsSpace;
import vcreature.phenotype.Block;
import vcreature.phenotype.Creature;
import com.jme3.scene.Node;
import com.jme3.math.Vector3f;
import vcreature.phenotype.EnumNeuronInput;
import vcreature.phenotype.Neuron;


/**
 * @author Alex Baker
 *
 * Procedurally generates a random creature
 */
public class RandomCreature extends Creature
{
  private RandomCreatureParameters params = new RandomCreatureParameters();

  private List<Block> body = new ArrayList<>();

  public RandomCreature(PhysicsSpace physicsSpace, Node jMonkeyRootNode)
  {
    super(physicsSpace, jMonkeyRootNode);
    generateCreature();
  }

  public void generateCreature()
  {
    if (body.size() != 0) remove();
    body.clear();
    Block root = generateBlock();
    body.add(root);
    //addBlocks(root);
    generateBlock(generateBlock(generateBlock(root)));
    placeOnGround();
  }

  private void addBlocks(Block parent)
  {
    int attempts = 0;
    Random rand = new Random();

    if (getDepth(parent, 0) > params.MAX_DEPTH)
    {
      return;
    }

    while (attempts < params.MAX_GENERATION_ATTEMPTS)
    {
      if (getChildren(parent).size() <= params.MAX_CHILDREN && rand.nextFloat() < params.CHILD_SPAWN_CHANCE)
      {
        Block child = generateBlock(parent);
        body.add(child);
        if (child != null && rand.nextFloat() < params.RECURSE_CHANCE)
        {
          addBlocks(child);
        }
      }
      attempts++;
    }
  }

  private Block generateBlock()
  {
    Random rand = new Random();
    Vector3f size = genRandSize(rand);

    float x = 0f;
    float y = 0f;
    float z = 0f;
    Vector3f center = new Vector3f(x, y, z);

    return addRoot(center, size);
  }

  private Block generateBlock(Block parent)
  {
    Random rand = new Random();
    Vector3f size = genRandSize(rand);
    Vector3f parentSize = new Vector3f(parent.getSizeX()/2, parent.getSizeY()/2, parent.getSize()/2);
    float[] angles = {0,0,0};

    int side = rand.nextInt(6);
    Vector3f pivot;
    Vector3f parentPivot;
    Vector3f axis;
    Vector3f parentAxis;

    float x = 0;
    float y = 0;
    float z = 0;

    switch (side) {
      case 0: // XY plane 1
        x = (parent.getSizeX()/2) - (rand.nextFloat()*parent.getSizeX());
        y = (parent.getSizeY()/2) - (rand.nextFloat()*parent.getSizeY());
        z = parentSize.z;
        parentPivot = new Vector3f(x, y, z);
        pivot = new Vector3f(0, 0, -size.z);
        if (rand.nextBoolean())
        {
          parentAxis = Vector3f.UNIT_X;
          axis = Vector3f.UNIT_X;
        }
        else
        {
          parentAxis = Vector3f.UNIT_Y;
          axis = Vector3f.UNIT_Y;
        }
        break;
      case 1: // XY plane 2
        x = (parent.getSizeX()/2) - (rand.nextFloat()*parent.getSizeX());
        y = (parent.getSizeY()/2) - (rand.nextFloat()*parent.getSizeY());
        z = -parentSize.z;
        parentPivot = new Vector3f(x, y, z);
        pivot = new Vector3f(0, 0, size.z);
        if (rand.nextBoolean())
        {
          parentAxis = Vector3f.UNIT_X;
          axis = Vector3f.UNIT_X;
        }
        else
        {
          parentAxis = Vector3f.UNIT_Y;
          axis = Vector3f.UNIT_Y;
        }
        break;
      case 2: // XZ plane 1
        x = (parent.getSizeX()/2) - (rand.nextFloat()*parent.getSizeX());
        y = parentSize.y;
        z = (parent.getSize()/2) - (rand.nextFloat()*parent.getSize());
        parentPivot = new Vector3f(x, y, z);
        pivot = new Vector3f(0, -size.y, 0);
        if (rand.nextBoolean())
        {
          parentAxis = Vector3f.UNIT_X;
          axis = Vector3f.UNIT_X;
        }
        else
        {
          parentAxis = Vector3f.UNIT_Z;
          axis = Vector3f.UNIT_Z;
        }
        break;
      case 3: // XZ plane 2
        x = (parent.getSizeX()/2) - (rand.nextFloat()*parent.getSizeX());
        y = -parentSize.y;
        z = (parent.getSize()/2) - (rand.nextFloat()*parent.getSize());
        parentPivot = new Vector3f(x, y, z);
        pivot = new Vector3f(0, size.y, 0);
        if (rand.nextBoolean())
        {
          parentAxis = Vector3f.UNIT_X;
          axis = Vector3f.UNIT_X;
        }
        else
        {
          parentAxis = Vector3f.UNIT_Z;
          axis = Vector3f.UNIT_Z;
        }
        break;
      case 4: // YZ plane 1
        x = parentSize.x;
        y = (parent.getSizeY()/2) - (rand.nextFloat()*parent.getSizeY());
        z = (parent.getSize()/2) - (rand.nextFloat()*parent.getSize());
        parentPivot = new Vector3f(x, y, z);
        pivot = new Vector3f(-size.x, 0, 0);
        if (rand.nextBoolean())
        {
          parentAxis = Vector3f.UNIT_Y;
          axis = Vector3f.UNIT_Y;
        }
        else
        {
          parentAxis = Vector3f.UNIT_Z;
          axis = Vector3f.UNIT_Z;
        }
        break;
      default: // YZ plane 2
        x = -parentSize.x;
        y = (parent.getSizeY()/2) - (rand.nextFloat()*parent.getSizeY());
        z = (parent.getSize()/2) - (rand.nextFloat()*parent.getSize());
        parentPivot = new Vector3f(x, y, z);
        pivot = new Vector3f(size.x, 0, 0);
        if (rand.nextBoolean())
        {
          parentAxis = Vector3f.UNIT_Y;
          axis = Vector3f.UNIT_Y;
        }
        else
        {
          parentAxis = Vector3f.UNIT_Z;
          axis = Vector3f.UNIT_Z;
        }
        break;
    }
    return addBlock(angles, size, parent, parentPivot, pivot, parentAxis, axis);
  }

  private float genRandDim(Random rand)
  {
    return params.MIN_BLOCK_DIM + (rand.nextFloat() * (params.MAX_BLOCK_DIM - params.MIN_BLOCK_DIM));
  }

  private Vector3f genRandSize(Random rand)
  {
    float width = genRandDim(rand); // x dimension
    float height = genRandDim(rand); // y dimension
    float depth = genRandDim(rand); // z dimension
    return new Vector3f(width/2, height/2, depth/2);
  }

  private List<Block> getChildren(Block parent)
  {
    List<Block> children = new ArrayList<>();
    for (Block block : body)
    {
      Block blockParent = getParent(block);
      if (blockParent != null && parent.getID() == blockParent.getID())
      {
        children.add(block);
      }
    }
    return children;
  }

  private int getDepth(Block block, int depth)
  {
    Block parent = getParent(block);
    if (parent == null)
    {
      return depth;
    }

    return getDepth(parent, depth+1);
  }

  private Block getParent(Block block)
  {
    Block parent;
    try
    {
      int parentID = block.getIdOfParent();
      if (parentID == -1)
      {
        parent = null;
      }
      else
      {
        parent = body.get(parentID);
      }
    }
    catch (NullPointerException ex)
    {
      parent = null;
    }
    return parent;
  }

  private Neuron genRandNeuron()
  {
    Random rand = new Random();

    EnumNeuronInput inputA = getRandInput(rand);
    EnumNeuronInput inputB = getRandInput(rand);
    EnumNeuronInput inputC = getRandInput(rand);
    EnumNeuronInput inputD = getRandInput(rand);
    EnumNeuronInput inputE = getRandInput(rand);

    Neuron neuron = new Neuron(inputA, inputB, inputC, inputD, inputE);
    neuron.setInputValue(Neuron.A, rand.nextInt(params.MAX_NEURON_VALUE));
    neuron.setInputValue(Neuron.B, rand.nextInt(params.MAX_NEURON_VALUE));
    neuron.setInputValue(Neuron.C, rand.nextInt(params.MAX_NEURON_VALUE));
    neuron.setInputValue(Neuron.D, rand.nextInt(params.MAX_NEURON_VALUE));
    neuron.setInputValue(Neuron.E, rand.nextInt(params.MAX_NEURON_VALUE));

    return neuron;
  }

  private EnumNeuronInput getRandInput(Random rand)
  {
    return EnumNeuronInput.values()[rand.nextInt(EnumNeuronInput.values().length)];
  }
}

package vcreature;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusiveWithComments;
import vcreature.phenotype.Block;
import vcreature.phenotype.Creature;
import vcreature.phenotype.EnumNeuronInput;
import vcreature.phenotype.Neuron;


/**
 *
 */
public class FlappyBird2 extends Creature
{
  public FlappyBird2(PhysicsSpace physicsSpace, Node rootNode)
  {
    super(physicsSpace, rootNode);

    float[] rotations = new float[] {0,0,0};
    Vector3f torsoSize = new Vector3f( 2.0f, 1.5f, 1.5f);
    Vector3f leg1Size  = new Vector3f( 3.0f, 0.5f, 1.0f);
    Vector3f leg2Size  = new Vector3f( 3.0f, 0.5f, 1.0f);
    Vector3f leg3Size  = new Vector3f( 1.0f, 0.5f, 3.0f);
    Vector3f leg4Size  = new Vector3f( 1.0f, 0.5f, 3.0f);

    Block torso = addRoot(new Vector3f(0, 100, 0), torsoSize, rotations);
    torso.setMaterial(Block.MATERIAL_RED);

    Vector3f pivotA = new Vector3f( 2.0f, -1.5f,  0.0f);
    Vector3f pivotB = new Vector3f(-3.0f,  0.5f,  0.0f);
    Block leg1 = addBlock(rotations, leg1Size, torso, pivotA,  pivotB, Vector3f.UNIT_Z);

    Vector3f pivotC = new Vector3f(-2.0f, -1.5f,  0.0f);
    Vector3f pivotD = new Vector3f( 3.0f,  0.5f,  0.0f);
    Block leg2 = addBlock(rotations, leg2Size, torso, pivotC,  pivotD, Vector3f.UNIT_Z);

    Vector3f pivotE = new Vector3f(0.0f, -1.5f, -1.5f);
    Vector3f pivotF = new Vector3f(0.0f, 0.5f, 3.0f);
    Block leg3 = addBlock(rotations, leg3Size, torso, pivotE, pivotF, Vector3f.UNIT_X);

    Vector3f pivotG = new Vector3f(0.0f, -1.5f, 1.5f);
    Vector3f pivotH = new Vector3f(0.0f, 0.5f, -3.0f);
    Block leg4 = addBlock(rotations, leg4Size, torso, pivotG, pivotH, Vector3f.UNIT_X);


    Neuron leg1Neuron1 = new Neuron(EnumNeuronInput.TIME, null, null, null, null);
    leg1Neuron1.setInputValue(Neuron.C, 5);
    leg1Neuron1.setInputValue(Neuron.D, -Float.MAX_VALUE);

    Neuron leg1Neuron2 = new Neuron(EnumNeuronInput.TIME, null, null, null, null);
    leg1Neuron2.setInputValue(Neuron.C, 4);
    leg1Neuron2.setInputValue(Neuron.D, Float.MAX_VALUE);

    leg1.addNeuron(leg1Neuron1);
    leg1.addNeuron(leg1Neuron2);

    Neuron leg2Neuron1 = new Neuron(EnumNeuronInput.TIME, null, null, null, null);
    leg2Neuron1.setInputValue(Neuron.C, 5);
    leg2Neuron1.setInputValue(Neuron.D, Float.MAX_VALUE);

    Neuron leg2Neuron2 = new Neuron(EnumNeuronInput.TIME, null, null, null, null);
    leg2Neuron2.setInputValue(Neuron.C, 4);
    leg2Neuron2.setInputValue(Neuron.D, -Float.MAX_VALUE);

    leg2.addNeuron(leg2Neuron1);
    leg2.addNeuron(leg2Neuron2);

    Neuron leg3Neuron1 = new Neuron(EnumNeuronInput.TIME, null, null, null, null);
    leg3Neuron1.setInputValue(Neuron.C, 5);
    leg3Neuron1.setInputValue(Neuron.D, -Float.MAX_VALUE);

    Neuron leg3Neuron2 = new Neuron(EnumNeuronInput.TIME, null, null, null, null);
    leg3Neuron2.setInputValue(Neuron.C, 4);
    leg3Neuron2.setInputValue(Neuron.D, Float.MAX_VALUE);

    leg3.addNeuron(leg3Neuron1);
    leg3.addNeuron(leg3Neuron2);

    Neuron leg4Neuron1 = new Neuron(EnumNeuronInput.TIME, null, null, null, null);
    leg4Neuron1.setInputValue(Neuron.C, 5);
    leg4Neuron1.setInputValue(Neuron.D, Float.MAX_VALUE);

    Neuron leg4Neuron2 = new Neuron(EnumNeuronInput.TIME, null, null, null, null);
    leg4Neuron2.setInputValue(Neuron.C, 4);
    leg4Neuron2.setInputValue(Neuron.D, -Float.MAX_VALUE);

    leg4.addNeuron(leg2Neuron1);
    leg4.addNeuron(leg2Neuron2);

    placeOnGround();
  }
}
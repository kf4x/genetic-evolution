package vcreature.Algorithms;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import vcreature.*;


public class GAMain extends MainSim implements ActionListener
{

  private static Environment environment;
  private Evolution evolution;
  int beingIndx = 0;
  private Population finalPopulation;

  public Environment getEnvironment() {
    return environment;
  }

  @Override
  public void simpleInitApp()
  {
    super.simpleInitApp();
//    environment = new Environment(getStateManager().getState(BulletAppState.class),
//                              assetManager,
//                              rootNode);
//    FlappyBird bird = new FlappyBird(environment.getBulletAppState().getPhysicsSpace(), rootNode);

//    evolution = new Evolution(environment);
//    evolution.getPopulation().add(new Being(bird));
//    GeneticAlgorithm GA = new GeneticAlgorithm(evolution.getPopulation(), this);
//    finalPopulation = GA.evolvePopulation();
    initKeys();


  }
  private void initKeys() {

    inputManager.addMapping("Update Creature",  new KeyTrigger(KeyInput.KEY_U));

    // Add the names to the action listener.
    inputManager.addListener(this,"Update Creature");
  }


  @Override
  public void onAction(String name, boolean isPressed, float timePerFrame)
  {
    super.onAction(name, isPressed, timePerFrame);

    if (isPressed && name.equals("Update Creature"))
    {
      beingIndx++;
      // environment.removeFromWorld(); // bug in creature.remove(); ????
      Being being = finalPopulation.get(beingIndx);
      // environment.addToWorld(being);

    }
  }

  public static void main(String[] args)
  {
    AppSettings settings = new AppSettings(true);
    settings.setResolution(1024, 768);
    settings.setSamples(4); //activate antialising (softer edges, may be slower.)

    //Set vertical syncing to true to time the frame buffer to coincide with the refresh frequency of the screen.
    //This also throttles the calls to simpleUpdate. Without this throttling, I get 1000+ pfs on my Alienware laptop
    //   Your application will have more work to do than to spend cycles rendering faster than the
    //   capture rate of the RED Camera used to shoot Lord of the Rings.
    settings.setVSync(true);
    settings.setFrequency(60);//Frames per second
    settings.setTitle("Flappy Bird Creature");

    System.out.println("Starting App");

    GAMain app = new GAMain();
    app.setShowSettings(false);
    app.setSettings(settings);
//    app.start();
    app.start(JmeContext.Type.Headless);


    //        app.evolution.addToWorld(new Being());
  }

  @Override
  public void simpleUpdate(float deltaSeconds)
  {
    super.simpleUpdate(deltaSeconds);
//    environment.update(deltaSeconds);

  }
}
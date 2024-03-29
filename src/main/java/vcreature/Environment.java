package vcreature;

/**
 * @author Javier Chavez
 * @author Alex Baker
 * @author Dominic Salas
 * @author Cari Martinez
 * <p>
 * Date November 4, 2015
 * CS 351
 * Genetic Evolution
 */


import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import vcreature.collections.EvolveManager;
import vcreature.collections.Population;
import vcreature.genotype.Genome;
import vcreature.genotype.GenomeGenerator;
import vcreature.morphology.GeneticAlgorithm;
import vcreature.morphology.HillClimb;
import vcreature.phenotype.*;
import vcreature.translations.CreatureSynthesizer;
import vcreature.translations.GenomeSynthesizer;
import vcreature.translations.TextSynthesizer;
import vcreature.utils.Statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 *  Environment housing all the applications main functionality.
 */
public class Environment extends AbstractApplication
{
  private float elapsedSimulationTime = 0.0f;
  private float totalSimTime = 0.0f;

  // Main population
  private Population population;

  // Wrapper for population and controlling populations
  private EvolveManager evolution;

  // Turning creatures into DNA and Blocks
  private CreatureSynthesizer creatureSynthesizer;
  private GenomeSynthesizer genomeSynthesizer;

  // Current creature, being, genome
  private Creature creature = null;
  private Being being = null;
  private Genome genome = null;

  // Used for crossing
  private GeneticAlgorithm breeding;
  private HillClimb mutating;

  // Generate random genomes
  private GenomeGenerator generator;

  // Generation currently spawning
  boolean newGenerationSpwan = false;

  private TextSynthesizer synthesizer;


  // Amount of time creatures are left in environment
  private static int EVALUATION_TIME = 6; // seconds

  // Used for getting random subpopulation for crossing
  private Random random = new Random();
  private boolean beingAdded;

  private boolean pauseEvaluation = false;
  private String file;

  public Environment(int i)
  {
    super();
    speed = i;
  }

  public Environment()
  {
    super();
  }

  public Statistics getStats()
  {
    return stats;
  }

  private Statistics stats;


  @Override
  public void simpleInitApp()
  {
    super.simpleInitApp();

    Block.initStaticMaterials(assetManager);

    creatureSynthesizer = new CreatureSynthesizer();
    genomeSynthesizer = new GenomeSynthesizer(getPhysicsSpace(), rootNode);


    breeding = new GeneticAlgorithm(this);
    mutating = new HillClimb(this);
    population = new Population(breeding, mutating);

    stats  = new Statistics(population);
    breeding.setDataHandler(stats);
    mutating.setDataHandler(stats);

    // initialize population
    generator = new GenomeGenerator(getPhysicsSpace(), rootNode);

    synthesizer = new TextSynthesizer();

    initPopulation();

    // set the population to a evolution
    evolution = new EvolveManager(population, stats);

    evolution.start();
    stats.init();
  }



  /* Use the main event loop to trigger repeating actions. */
  @Override
  public void simpleUpdate(float deltaSeconds)
  {
    super.simpleUpdate(deltaSeconds);

    elapsedSimulationTime += deltaSeconds;
    stats.update(deltaSeconds);
    totalSimTime += deltaSeconds;


    if (!pauseEvaluation)
    {

      // A being is in queue ready for evaluation
      if (beingAdded)
      {
        genome = being.getGenotype();

        creature = genomeSynthesizer.encode(genome);
        beingAdded = false;
        elapsedSimulationTime = 0;
      }


      // check if the evaluation is complete
      if (creature != null && elapsedSimulationTime >= EVALUATION_TIME)
      {
        being.setFitness(creature.getFitness());
        being.setUnderEvaluation(false);

        creature.remove();
        getPhysicsSpace().distributeEvents();
        creature = null;
      }

      // On cross populations when one is complete.
      // avoid adding more than more to engine
      if (!evolution.isEvolving())
      {
        System.out.println("New thread.");
        newGenerationSpwan = true;
        new Thread(() -> {
          evolution.interrupt();
        }).start();
      }
    }
    // update the brain
    if (creature != null)
    {
      creature.updateBrain(elapsedSimulationTime);
    }

  }

  /**
   * Add a being into the environment for evaluation.
   *
   * @param v Being with genotype to be added to physics space.
   */
  public void beginEvaluation(Being v)
  {
    this.being = v;
    being.setUnderEvaluation(true);
    beingAdded = true;
    genome = being.getGenotype();
  }

  public Population getPopulation()
  {
    return population;
  }


  private int genRandDim(int max)
  {
    return random.nextInt(max - 1) + 0;
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
    settings.setFrequency(60); //Frames per second

    Environment app = new Environment();
    app.setShowSettings(false);
    app.setSettings(settings);

    if (args.length > 0)
    {
      if (args[0].equalsIgnoreCase("headless"))
      {
        app.start(JmeContext.Type.Headless);
        app.speed = 4;
      }
    }
    //app.start();
  }

  public void setFile(String file)
  {
    this.file = file;
  }

  private void initPopulation()
  {
    if (file !=null && !file.equals(""))
    {
      ArrayList<Being> readin = synthesizer.encode(new File(file));
      for (Being being1 : readin)
      {
        population.add(being1);
      }
      Collections.sort(population);
    }
    else
    {
      // Fill up the population
      // for (int i = 0; i < 5; i++)
      // {

        // FlappyBird _creature = new FlappyBird(getPhysicsSpace(), rootNode);
        //      FlappyBird3 _creature3 = new FlappyBird3(getPhysicsSpace(), rootNode);
        //      FlappyBird4 _creature4 = new FlappyBird4(getPhysicsSpace(), rootNode);
        //      FlappyBird5 _creature5 = new FlappyBird5(getPhysicsSpace(), rootNode);

//        Genome _genome = creatureSynthesizer.encode(_creature);
//        _creature.remove();
//        Being bb = new Being();
//        bb.setGenotype(_genome);
//        population.add(bb);


        //      Genome _genome3 = creatureSynthesizer.encode(_creature3);
        //      _creature3.remove();
        //      Being bb3 = new Being();
        //      bb3.setGenotype(_genome3);
        //      population.add(bb3);



        for (int x = 0; x < 40; x++)
        {
          Being _randBeing = new Being();
          _randBeing.setGenotype(generator.generateGenome());
          population.add(_randBeing);

        }
        //
        //      Genome _genome4 = creatureSynthesizer.encode(_creature4);
        //      _creature4.remove();
        //      Being bb4 = new Being();
        //      bb4.setGenotype(_genome4);
        //      population.add(bb4);
        //
        //      Genome _genome5 = creatureSynthesizer.encode(_creature5);
        //      _creature5.remove();
        //      Being bb5 = new Being();
        //      bb5.setGenotype(_genome5);
        //      population.add(bb5);

      // }

    }
  }

  public void togglePause()
  {
    pauseEvaluation = !pauseEvaluation;
  }
}

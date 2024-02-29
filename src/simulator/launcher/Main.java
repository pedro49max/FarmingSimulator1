package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.model.Simulator;
import simulator.misc.Utils;
import simulator.control.Controller;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
//papure
public class Main {
	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _deltaTime = null;
	private static Double _time = null;
	private static String _in_file = null;
	private static String _out_file = null;
	private static boolean _simple_view = false;
	private static ExecMode _mode = ExecMode.BATCH;

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_delta_time_option(line);
			parse_output_option(line);
			parse_simple_viewer_option(line);
			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();
		// delta
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").desc("A double representing actual time, in seconds, per simulation step. Default value: 0.03.").build());
		
		
		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());
		
		//simple viewer
		cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());
		
		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());

		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}
	private static void parse_delta_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("dt", _default_time.toString());
		try {
			_deltaTime = Double.parseDouble(t);
			assert (_deltaTime>= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	
    }
	private static void parse_output_option(CommandLine line) throws ParseException{
		_out_file = line.getOptionValue("o");
		if (_mode == ExecMode.BATCH && _out_file == null) {
			throw new ParseException("In batch mode an output configuration file is required");
		}
    }
	private static void parse_simple_viewer_option(CommandLine line) {
		if (line.hasOption("sv")) {
			_simple_view = true;
		}
		else
			_simple_view = false;
    }

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}

	private static void init_factories() {
		
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}


	private static void start_batch_mode() throws Exception {
		try (InputStream is = new FileInputStream(new File(_in_file));
	            OutputStream os = new FileOutputStream(new File(_out_file))) {
	        // Load the input JSON file
	        JSONObject inputJSON = loadJSONFile(is);

	        // Extract parameters from the input JSON
	        int width = inputJSON.getInt("width");
	        int height = inputJSON.getInt("height");
	        int cols = inputJSON.getInt("cols");
	        int rows = inputJSON.getInt("rows");
	        JSONArray animalsArray = inputJSON.getJSONArray("animals");

	        // Create the simulator instance
	        Simulator simulator = new Simulator(cols, rows, width, height);

	        // Create animals based on the specifications provided in the input file
	        for (int i = 0; i < animalsArray.length(); i++) {
	            JSONObject animalSpec = animalsArray.getJSONObject(i);
	            int amount = animalSpec.getInt("amount");
	            JSONObject animalData = animalSpec.getJSONObject("spec");
	            String animalType = animalData.getString("type");

	            // Create animals based on the type and amount specified
	            for (int j = 0; j < amount; j++) {
	                switch (animalType) {
	                    case "sheep":
	                        simulator.add_animal(new SheepBuilder().create_instance(animalData));
	                        break;
	                    case "wolf":
	                        simulator.add_animal(new WolfBuilder().create_instance(animalData));
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid animal type: " + animalType);
	                }
	            }
	        }

	        // Create the controller instance and run the simulation
	        Controller controller = new Controller(simulator);
	        controller.run(_time, _deltaTime, _simpleViewer, os);
	    }
	}

	private static void start_GUI_mode() throws Exception {
		throw new UnsupportedOperationException("GUI mode is not ready yet ...");
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}

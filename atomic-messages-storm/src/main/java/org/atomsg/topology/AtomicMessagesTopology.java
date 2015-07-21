package org.atomsg.topology;

	// Begin imports

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.atomsg.bolt.MessageAggregatorBolt;
import org.atomsg.bolt.MessageSplitterBolt;
import org.atomsg.bolt.MessageWriterBolt;
import org.atomsg.spout.GeneratorSpout;
import org.atomsg.spout.MessageReaderSpout;
import org.atomsg.util.AlarmClock;

import backtype.storm.Config;
import backtype.storm.ILocalCluster;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;

	// End imports
	
public class AtomicMessagesTopology {

    static final String DEFAULT_TARGET  = "dev";

	public static boolean quiesce = false;

    private static Config config = null;
    static List<String> TARGETS = Arrays.asList(new String[] {"dev", "sandbox", "cluster"});

    private static ILocalCluster localCluster = new LocalCluster();
    private static final Logger log = Logger.getLogger(AtomicMessagesTopology.class);

    public static void main(String[] args) throws Exception {

			// Begin topology execution code

    	String target = DEFAULT_TARGET;
    	
    	for (int i = 0; i < args.length; i++) {
    		System.out.println("args["+i+"] = "+args[i]);
    	}
     
//    	if (args.length > 0 && TARGETS.contains(args[0])) {
//    		target = args[0];
//        } else if (args.length > 0) {
//        	// absolute filepath must have been specified
//        	
//        }

        config = loadConfig(args[0]);
        
        config.put("atomic.message.group", new Long(System.currentTimeMillis()));

		// Handles the case where monitoring is turned off or not requested
        // TaskHook.registerTo(config);
        
       	if (isRunLocally()) {
       		submitTopologyLocal();
       	} else {
       		submitTopology();
       	}
       	
			// End topology execution code

    }
    
    public static void submitTopology() {
    
    	StormTopology topology = createTopology();
       	String topologyName = config.get("topology.name").toString();

       	try {
			StormSubmitter.submitTopology(topologyName, config, topology);
		} catch (AlreadyAliveException e) {
			log.error("AtomicMessagesTopology Topology was already alive: "+e);
		} catch (InvalidTopologyException e) {
			log.error("AtomicMessagesTopology Invalid topology: "+e);
		}

    }
    
    public static void submitTopologyLocal() {
    
    	StormTopology topology = createTopology();
       	String topologyName = config.get("topology.name").toString();

		try {
			getLocalCluster().submitTopology(topologyName, config, topology);
			
			int sleepMinutes;
			try {
				sleepMinutes = ((Integer) config.get("sleep.after.submit.minutes")).intValue();
			} catch (NumberFormatException e) {
				sleepMinutes = 1;
			}
			AlarmClock.go(sleepMinutes * 60000,5*60000);
			
			getLocalCluster().killTopology(topologyName);
			getLocalCluster().shutdown();
		} catch (AlreadyAliveException e) {
			log.error("AtomicMessagesTopology Topology was already alive: "+e);
		} catch (InvalidTopologyException e) {
			log.error("AtomicMessagesTopology Invalid topology: "+e);
		} catch (NotAliveException e) {
			log.error("AtomicMessagesTopology Topology is not yet alive: "+e);
		}

    }

    public static Config loadConfig(String env) {
        log.info("AtomicMessagesTopology Initializing " + env + " topology");
        Properties props = new Properties();
        Config config = new Config();
        try {
        	InputStream is = AtomicMessagesTopology.class.getResourceAsStream("/" + env + ".properties");
        	if (is == null) {
        		is = new FileInputStream(env);
        	}
           props.load(is);
           for (Object prop : props.keySet()) {
				Object value = props.get(prop);
				try {
					// see if it's an Integer
					int intValue = Integer.parseInt(String.valueOf(value).trim());
					config.put(prop.toString(), intValue);
				} catch (NumberFormatException e) {
					config.put(prop.toString(), value);					
				}
				
				log.info("AtomicMessagesTopology "+ prop.toString() + "=" + value);
			}
            config.put(Config.TOPOLOGY_FALL_BACK_ON_JAVA_SERIALIZATION, true);
        } catch(IOException e) {
			log.error("AtomicMessagesTopology Error loading configuration: "+e);
        }
        return config;
    }

    public static StormTopology createTopology() {

        TopologyBuilder builder = new TopologyBuilder();
		BoltDeclarer boltDeclarer;
		
		int parallelismHint = 0;

        parallelismHint = (Integer)config.get("Generator.parallelismHint");
        builder.setSpout("Generator", new GeneratorSpout(), parallelismHint);

        parallelismHint = (Integer)config.get("MessageReader.parallelismHint");
        builder.setSpout("MessageReader", new MessageReaderSpout(), parallelismHint);

        parallelismHint = (Integer)config.get("MessageSplitter.parallelismHint");
        boltDeclarer = builder.setBolt("MessageSplitter", new MessageSplitterBolt(), parallelismHint);
        boltDeclarer.shuffleGrouping("Generator","NewMessages");
        boltDeclarer.shuffleGrouping("MessageReader","OldMessages");

        parallelismHint = (Integer)config.get("MessageWriter.parallelismHint");
        boltDeclarer = builder.setBolt("MessageWriter", new MessageWriterBolt(), parallelismHint);
        boltDeclarer.shuffleGrouping("MessageSplitter","SplitMessages");

        parallelismHint = (Integer)config.get("MessageAggregator.parallelismHint");
        boltDeclarer = builder.setBolt("MessageAggregator", new MessageAggregatorBolt(), parallelismHint);
        boltDeclarer.shuffleGrouping("MessageSplitter","AtomicMessages");

        return builder.createTopology();
    }
    
    private static ILocalCluster getLocalCluster() {
    	return localCluster;
    }
    
    public static void setLocalCluster(ILocalCluster cluster) {
    	localCluster = cluster;
    }
    
    public static boolean isRunLocally() {
        return Boolean.parseBoolean(config.get("topology.run.locally").toString());
    }

	// Begin custom methods
	
	
	
	// End custom methods

}

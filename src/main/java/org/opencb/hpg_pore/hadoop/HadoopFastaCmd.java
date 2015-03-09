package org.opencb.hpg_pore.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.Tool;
import org.opencb.hpg_pore.NativePoreSupport;

public class HadoopFastaCmd extends Configured implements Tool {
	public static class Map extends Mapper<Text, BytesWritable, NullWritable, Text> {

		private MultipleOutputs<NullWritable, Text> multipleOutputs = null; 
		private String library;
		@Override
		public void setup(Context context) {
			
			Configuration conf = context.getConfiguration();
			library = conf.get("lib",System.getenv("LD_LIBRARY_PATH"));
			
			NativePoreSupport.loadLibrary(library);

			multipleOutputs = new MultipleOutputs<NullWritable, Text>(context);
		}

		@Override
		protected void cleanup(Context context) throws IOException,
		InterruptedException {
			multipleOutputs.close();
		}		

		@Override
		public void map(Text key, BytesWritable value, Context context) throws IOException, InterruptedException {
			System.out.println("***** map: key = " + key);

			String info = new NativePoreSupport().getFastqs(value.getBytes());

			if (info.length() <= 0) return;

			byte[] brLine = new byte[1];
			brLine[0] = '\n';

			String[] lines = info.split("\n");
			String name = null;
			Text content = null;

			String line;

			System.out.println("info length = " + info.length() + ", num.lines = " + lines.length);

			for (int i = 0; i < lines.length; i += 5) {
				// first line: runId & template/complement/2d				
				line = lines[i];
				System.out.println(i + " of " + lines.length + " : " + line);
				name = new String(line);

				// second line: read ID
				line = lines[i + 1];
				content = new Text("> " + line.substring(1) + "\n");

				// third line: nucleotides
				line = lines[i + 2];
				content.append(line.getBytes(), 0, line.length());

				multipleOutputs.write(NullWritable.get(), content, name);
			}
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("lib", args[2]);
		Job job = new Job(conf, "hadoop-pore-fasta");
		job.setJarByClass(HadoopFastaCmd.class);

		String srcFileName = args[0];
		String outDirName = args[1];

		// add input files to mapreduce processing
		FileInputFormat.addInputPath(job, new Path(srcFileName));
		job.setInputFormatClass(SequenceFileInputFormat.class);

		// set output file
		FileOutputFormat.setOutputPath(job, new Path(outDirName));

		// set map, combine, reduce...
		job.setMapperClass(Map.class);
		job.setNumReduceTasks(0);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);

		return (job.waitForCompletion(true) ? 0 : 1);
	}
}

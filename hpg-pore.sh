#!/usr/bin/env bash

function print_usage(){
  echo "Usage: hpg-pore.sh COMMAND"
  echo "       where COMMAND is one of:"
  echo ""
  echo "  stats        explore Fast5 reads by computing statistics and plotting charts"
  echo "  signal       plot the measured signal for a given Fast5 read"
  echo "  events       extract events in text file for a given Fast5 read"
  echo "  fastq        extract the sequences in Fastq format for a set of Fast5 reads"
  echo "  fasta        extract the sequences in Fasta format for a set of Fast5 reads"
  echo "  fast5names   extract all Fast5 names stored in the HDFS Hadoop file"
  echo ""
  echo "Previous commands can run both on a local system and on a Hadoop environment (for the latter, use the option --hadoop."
  echo "Before executing those commands on a Hadoop environment, you must copy your Fast5 files to the Hadoop file system by running the command:"
  echo ""
  echo "  import    copy the Fast5 files into the Hadoop environment (a HDFS Hadoop file)"
  echo ""
  echo "To get back your Fast5 files imported to the Hadoop environment, use the command:e Hadoop file system by running the command:"
  echo ""
  echo "  export   	copy back your Fast5 files in the local filesystem from the Hadoop environment"
  echo ""
  echo "Other commands:"
  echo ""
  echo "  version   print the version"
  echo "  help      print this help"
  echo ""
  echo "Most commands print help when invoked w/o parameters."
}

if [ $# = 0 ]; then
  print_usage
  exit
fi

HADOOP=0

#JAR=$1
#if [ ! -e "$JAR" ]; then
#  echo "Error: Jar $JAR does not exist!"
#  echo
#  print_usage
#  exit
#fi

COMMAND=$1
case $COMMAND in
  # usage flags
  --help|-help|-h)
    print_usage
    exit
    ;;
    
  # hadoop commands
  import|fast5names|export)
  	HADOOP=1
  	;;
  
  # command commands (hadoop or local commands)
  stats|fastq|fasta|signal|events|version)
  	for var in "$@"
	do
    	if [ $var = "--hadoop" ]; then
    		HADOOP=1
    	fi
	done
  	;;
  
  # default: invalid commands  
  *)
    echo "Unknown command: $COMMAND"
    print_usage
    exit
    ;;
esac

if [ $HADOOP -eq 1 ]; then
	# echo "hadoop jar $@"
	hadoop jar hpg-pore-0.1.0-jar-with-dependencies.jar $@
else
	# echo "java -jar $@"
	java -jar hpg-pore-0.1.0-jar-with-dependencies.jar $@
fi


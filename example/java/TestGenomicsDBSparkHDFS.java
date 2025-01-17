/**
 * The MIT License (MIT)
 * Copyright (c) 2018 Univeristy of Caifornia, Los Angeles and Intel Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS 
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.genomicsdb.spark.GenomicsDBConfiguration;
import org.genomicsdb.spark.GenomicsDBInputFormat;
import org.genomicsdb.importer.extensions.JsonFileExtensions;
import org.genomicsdb.model.GenomicsDBExportConfiguration;
import org.genomicsdb.model.GenomicsDBImportConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Base64;

import org.genomicsdb.shaded.com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.JsonFormat.ParseException;

public final class TestGenomicsDBSparkHDFS {

  public static void main(final String[] args) throws IOException,
        org.json.simple.parser.ParseException {
    LongOpt[] longopts = new LongOpt[8];
    longopts[0] = new LongOpt("loader", LongOpt.REQUIRED_ARGUMENT, null, 'l');
    longopts[1] = new LongOpt("query", LongOpt.REQUIRED_ARGUMENT, null, 'q');
    longopts[2] = new LongOpt("hostfile", LongOpt.REQUIRED_ARGUMENT, null, 'h');
    longopts[3] = new LongOpt("template_vcf_header", LongOpt.REQUIRED_ARGUMENT, null, 't');
    longopts[4] = new LongOpt("spark_master", LongOpt.REQUIRED_ARGUMENT, null, 's');
    longopts[5] = new LongOpt("jar_dir", LongOpt.REQUIRED_ARGUMENT, null, 'j');
    longopts[6] = new LongOpt("use-query-protobuf", LongOpt.NO_ARGUMENT, null, 'p');
    longopts[7] = new LongOpt("use-loader-protobuf", LongOpt.NO_ARGUMENT, null, 'e');

    if (args.length < 10) {
      System.err.println("Usage:\n\t--loader <loader.json> --query <query.json> --hostfile <hostfile>"
            +" --template_vcf_header <templateVCFHeader> --spark_master <sparkMaster> --jar_dir <jar_dir>"
            +"\nOptional args:\n --hostfile <hostfile> --use-query-protobuf --user-loader-protobuf");
      System.exit(-1);
    }
    String loaderFile, queryFile, hostfile, templateVCFHeader, sparkMaster, jarDir;
    boolean useQueryProtobuf = false;
    boolean useLoaderProtobuf = false;
    loaderFile = queryFile = hostfile = templateVCFHeader = sparkMaster = jarDir = "";
    Getopt g = new Getopt("TestGenomicsDBSparkHDFS", args, "l:q:h:t:s:j:p:e", longopts);
    int c = -1;
    String optarg;

    while ((c = g.getopt()) != -1) {
      switch (c) {
        case 'l':
          loaderFile = g.getOptarg();
          break;
        case 'q':
          queryFile = g.getOptarg();
          break;
        case 'h':
          hostfile = g.getOptarg();
          break;
        case 't':
          templateVCFHeader = g.getOptarg();
          break;
        case 's':
          sparkMaster = g.getOptarg();
          break;
        case 'j':
          jarDir = g.getOptarg();
          break;
        case 'p':
          useQueryProtobuf = true;
          break;
        case 'e':
          useLoaderProtobuf = true;
          break;
        default:
          System.err.println("Unknown command line option "+g.getOptarg());
          System.exit(-1);
      }
    }
    SparkConf conf = new SparkConf().setAppName("TestGenomicsDBSparkHDFS");

    Path dstdir = Paths.get("").toAbsolutePath();
    Path qSrc = Paths.get(queryFile);
    Path lSrc = Paths.get(loaderFile);
    File qDstFile = null;
    File lDstFile = null;
    JavaSparkContext sc = new JavaSparkContext(conf);
    if(!useQueryProtobuf) {
      qDstFile = File.createTempFile("query", ".json", new File(dstdir.toString()));
      qDstFile.deleteOnExit();
      Files.copy(qSrc, qDstFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      sc.addFile(qDstFile.getName());
    }
    if (!useLoaderProtobuf) {
      lDstFile = File.createTempFile("loader", ".json", new File(dstdir.toString()));
      lDstFile.deleteOnExit();
      Files.copy(lSrc, lDstFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      sc.addFile(lDstFile.getName());
    }
    Configuration hadoopConf = sc.hadoopConfiguration();
    if(!useLoaderProtobuf) {
      hadoopConf.set(GenomicsDBConfiguration.LOADERJSON, lDstFile.getName());
    }
    else {
      String pbString = JsonFileExtensions.getProtobufAsBase64StringFromFile(GenomicsDBImportConfiguration.ImportConfiguration.newBuilder(),
                                                        loaderFile);
      hadoopConf.set(GenomicsDBConfiguration.LOADERPB, pbString);
    }
    if(!useQueryProtobuf) {
      hadoopConf.set(GenomicsDBConfiguration.QUERYJSON, qDstFile.getName());
    }
    else {
      String pbString = JsonFileExtensions.getProtobufAsBase64StringFromFile(GenomicsDBExportConfiguration.ExportConfiguration.newBuilder(),
                                                        queryFile);
      hadoopConf.set(GenomicsDBConfiguration.QUERYPB, pbString);
    }
    if(!hostfile.isEmpty()) {
      hadoopConf.set(GenomicsDBConfiguration.MPIHOSTFILE, hostfile);
    }

    JavaPairRDD<String, VariantContext> variants;
    Class gformatClazz = GenomicsDBInputFormat.class;
    variants = sc.newAPIHadoopRDD(hadoopConf,
                                    gformatClazz, String.class, VariantContext.class);

    // sort based on variantcontext start pos. this is limited and will not work when data for more
    // than a single contig will be used. good enough for testing?
    List<VariantContext> result = variants.map(x -> x._2).sortBy(x -> {return x.getStart();}, true, 1).collect();

    // probably a smarter way to do this but...creating a temporary vcf file to 
    // grab the header from it for this test. Trying to create a VCFFileReader
    // with just the inputs/template_vcf_header doesn't work...
    // TODO: need to amend GenomicsDBRDD with function to write VCF file...
    File tempFile = File.createTempFile("temp",".vcf");
    tempFile.deleteOnExit();
    BufferedReader br = new BufferedReader(new FileReader(templateVCFHeader));
    BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
    String line;
    while ((line=br.readLine()) != null) {
      bw.write(line+'\n');
    }
    bw.write("#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT\t");
    bw.write(String.join("\t", result.get(0).getSampleNamesOrderedByName()));
    bw.write("\n");
    bw.write("1	12145	.	C	<NON_REF>	.	.	END=12277;DS	GT:DP:GQ:MIN_DP:PL	0/0:3:0:0:0,0,0\n");
    bw.close();
    br.close();
    VCFFileReader vcffr = new VCFFileReader(tempFile, false);
    
    final VariantContextWriter writer =
      new VariantContextWriterBuilder().setOutputVCFStream(System.out).unsetOption(
          Options.INDEX_ON_THE_FLY).build();
    writer.writeHeader(vcffr.getFileHeader());
    vcffr.close();

    // print rest of vcf
    for (VariantContext vc : result) {
      writer.add(vc);
    }
  }
}

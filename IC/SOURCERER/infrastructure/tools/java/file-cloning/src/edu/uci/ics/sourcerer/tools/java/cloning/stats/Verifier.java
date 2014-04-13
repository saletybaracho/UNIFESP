package edu.uci.ics.sourcerer.tools.java.cloning.stats;
///* 
// * Sourcerer: an infrastructure for large-scale source code analysis.
// * Copyright (C) by contributors. See CONTRIBUTORS.txt for full list.
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
//
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Public License for more details.
//
// * You should have received a copy of the GNU General Public License
// * along with this program. If not, see <http://www.gnu.org/licenses/>.
// */
//package edu.uci.ics.sourcerer.clusterer.cloning.stats;
//
//import static edu.uci.ics.sourcerer.util.io.Logging.logger;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.util.Map;
//import java.util.logging.Level;
//
//import edu.uci.ics.sourcerer.clusterer.cloning.method.dir.DirectoryClusterer;
//import edu.uci.ics.sourcerer.clusterer.cloning.method.fingerprint.FingerprintClusterer;
//import edu.uci.ics.sourcerer.clusterer.cloning.method.fqn.FqnClusterer;
//import edu.uci.ics.sourcerer.clusterer.cloning.method.hash.HashingClusterer;
//import edu.uci.ics.sourcerer.util.Counter;
//import edu.uci.ics.sourcerer.util.Helper;
//import edu.uci.ics.sourcerer.util.io.FileUtils;
//import edu.uci.ics.sourcerer.util.io.Property;
//import edu.uci.ics.sourcerer.util.io.TablePrettyPrinter;
//import edu.uci.ics.sourcerer.util.io.properties.StringProperty;
//
///**
// * @author Joel Ossher (jossher@uci.edu)
// */
//public class Verifier {
//  private static final int DIR_METHOD = 0x01;
//  private static final int HASH_METHOD = 0x02;
//  private static final int FQN_METHOD = 0X04;
//  private static final int FINGERPRINT_METHOD = 0x08;
//  
//  public static final Property<String> INTERSECTION_FILE_LISTING = new StringProperty("intersection-file", "intersection-file.txt", "List of intersection of files from all methods.");
//  public static final Property<String> INTERSECTION_PROJECT_SIZES = new StringProperty("intersection-project-sizes", "intersection-project-sizes.txt", "");
//  
//  /**
//   * Compares the file listings generated by each method,
//   * highlights differences, and generates a list of the files
//   * in the intersection of the methods.
//   */
//  public static void generateIntersectionFile() {
//    // Keep a map of all the files
//    Map<String, Integer> files = Helper.newHashMap();
//    
//    // Load the file listing from the directory method
//    logger.info("Loading directory file listing...");
//    for (String file : DirectoryClusterer.loadFileListing()) {
//      if (files.containsKey(file)) {
//        logger.log(Level.SEVERE, "Duplicated file: " + file);
//      } else {
//        files.put(file, DIR_METHOD);
//      }
//    }
//    
//    // Load the file listing from the hashing method
//    logger.info("Loading hashing file listing...");
//    Integer combined = DIR_METHOD | HASH_METHOD;
//    for (String file : HashingClusterer.loadFileListing()) {
//      Integer val = files.get(file);
//      if (val == null) {
//        files.put(file, HASH_METHOD);
//      } else if ((val.intValue() & HASH_METHOD) == HASH_METHOD){
//        logger.log(Level.SEVERE, "Duplicated file: " + file);
//      } else {
//        files.put(file, combined);
//      }
//    }
//    
//    // Load the file listing from the fqn method
//    logger.info("Loading fqn file listing...");
//    for (String file : FqnClusterer.loadFileListing()) {
//      Integer val = files.get(file);
//      if (val == null) {
//        files.put(file, FQN_METHOD);
//      } else if ((val.intValue() & FQN_METHOD) == FQN_METHOD) {
//        logger.log(Level.SEVERE, "Duplicated file: " + file);
//      } else {
//        files.put(file, val.intValue() | FQN_METHOD);
//      }
//    }
//    
//    // Load the file listing from the fingerprint method
//    logger.info("Loading fingerprint file listing...");
//    for (String file : FingerprintClusterer.loadFileListing()) {
//      Integer val = files.get(file);
//      if (val == null) {
//        files.put(file, FINGERPRINT_METHOD);
//      } else if ((val.intValue() & FINGERPRINT_METHOD) == FINGERPRINT_METHOD) {
//        logger.log(Level.SEVERE, "Duplicated file: " + file);
//      } else {
//        files.put(file, val.intValue() | FINGERPRINT_METHOD);
//      }
//    }
//    
//    // Verify the results
//    logger.info("Tabulating results...");
//    int[] results = new int[16];
//    for (int i = 0; i < results.length; i++) {
//      results[i] = 0;
//    }
//    for (Map.Entry<String, Integer> entry : files.entrySet()) {
//      results[entry.getValue()]++;
//    }
//
//    TablePrettyPrinter printer = TablePrettyPrinter.getLoggerPrettyPrinter();
//    printer.beginTable(2);
//    printer.addDividerRow();
//    printer.addRow("Methods", "Count");
//    printer.addDividerRow();
//    for (int i = 1; i < 16; i++) {
//      printer.beginRow();
//      StringBuilder types = new StringBuilder();
//      if ((i & DIR_METHOD) == DIR_METHOD) {
//        types.append("DIR ");
//      }
//      if ((i & HASH_METHOD) == HASH_METHOD) {
//        types.append("hash ");
//      }
//      if ((i & FQN_METHOD) == FQN_METHOD) {
//        types.append("fqn ");
//      }
//      if ((i & FINGERPRINT_METHOD) == FINGERPRINT_METHOD) {
//        types.append("fingerprint ");
//      }
//      printer.addCell(types.toString());
//      printer.addCell(results[i]);
//    }
//    printer.addDividerRow();
//    printer.endTable();
//    
//    BufferedWriter bw = null;
//    try {
//      bw = FileUtils.getBufferedWriter(INTERSECTION_FILE_LISTING);
//      for (Map.Entry<String, Integer> entry : files.entrySet()) {
//        if (entry.getValue() == 15) {
//          bw.write(entry.getKey() + "\n");
//        }
//      }
//    } catch (IOException e) {
//      logger.log(Level.SEVERE, "Unable to write intersection file.", e);
//    } finally {
//      FileUtils.close(bw);
//    }
//  }
//  
//  public static void computeIntersectionProjectSizes() {
//    BufferedReader br = null;
//    BufferedWriter bw = null;
//    
//    try {
//      br = FileUtils.getBufferedReader(INTERSECTION_FILE_LISTING);
//      bw = FileUtils.getBufferedWriter(INTERSECTION_PROJECT_SIZES);
//      
//      Map<String, Counter<String>> map = Helper.newHashMap();
//      for (String line = br.readLine(); line != null; line = br.readLine()) {
//        int colon = line.indexOf(':');
//        String project = line.substring(0, colon);
//        Counter<String> counter = map.get(project);
//        if (counter == null) {
//          counter = new Counter<String>(project);
//          map.put(project, counter);
//        }
//        counter.increment();
//      }
//      
//      for (Counter<String> counter : map.values()) {
//        bw.write(counter.getObject() + " " + counter.getCount() + "\n");
//      }
//    } catch (IOException e) {
//      logger.log(Level.SEVERE, "Error in computing intersection project sizes.", e);
//    } finally {
//      FileUtils.close(br);
//      FileUtils.close(bw);
//    }
//  }
//  
//  public static void generateFilteredList() {
//    Filter filter = FileFilter.loadFilter(INTERSECTION_FILE_LISTING);
//    DirectoryClusterer.generateFilteredListing(filter);
//    filter = FileFilter.loadFilter(INTERSECTION_FILE_LISTING);
//    HashingClusterer.generateFilteredList(filter);
//    filter = FileFilter.loadFilter(INTERSECTION_FILE_LISTING);
//    FqnClusterer.generateFilteredList(filter);
//    filter = FileFilter.loadFilter(INTERSECTION_FILE_LISTING);
//    FingerprintClusterer.generateFilteredList(filter);
//  }
//}

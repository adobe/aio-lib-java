/*
 * Copyright 2017 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.aio.event.journal;

import feign.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LocalTest {

  public static void main1(String[] args) {

    while (true) {

      Pattern pattern = Pattern.compile("<(.+)>;(\".+\")");
      String link = "</events/organizations/6399/integrations/44436/7ec25c68-94b5-4fe7-a079-31c238930aa0?seek={duration}&limit={count}>; rel=\"seek\"";
      Matcher matcher = pattern.matcher(link);
      boolean found = false;
      while (matcher.find()) {
        System.out.println("I found the text " + matcher.group() + " starting at index " +
            matcher.start() + " and ending at index " + matcher.end());
        found = true;
      }
      if (!found) {
        System.out.println("No match found.");
      }
    }
  }


  public static final String EXAMPLE_TEST_2 = "This is my small example string which I'm going to use for pattern matching.";

  public static void main2(String[] args) {
    Pattern pattern = Pattern.compile("\\w+");
    // in case you would like to ignore case sensitivity,
    // you could use this statement:
    // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(EXAMPLE_TEST_2);
    // check all occurance
    while (matcher.find()) {
      System.out.print("Start index: " + matcher.start());
      System.out.print(" End index: " + matcher.end() + " ");
      System.out.println(matcher.group());
    }
    // now create a new pattern and matcher to replace whitespace with tabs
    Pattern replace = Pattern.compile("\\s+");
    Matcher matcher2 = replace.matcher(EXAMPLE_TEST);
    System.out.println(matcher2.replaceAll("\t"));
  }

  public static final String LINK = "  </events/organizations/6399/integrations/44436/7ec25c68-94b5-4fe7-a079-31c238930aa0?seek={duration}&limit={count}>  ;   rel = \"seek\"";


  public static void main3(String[] args) {
    //Pattern pattern = Pattern.compile("\\w+");

    Pattern pattern = Pattern.compile("(<)(.+)(>)");

    // in case you would like to ignore case sensitivity,
    // you could use this statement:
    // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(LINK);
    // check all occurance
    while (matcher.find()) {
      System.out.print("Start index: " + matcher.start());
      System.out.print(" End index: " + matcher.end() + " ");
      System.out.println(matcher.group());
    }
    // now create a new pattern and matcher to replace whitespace with tabs
    Pattern replace = Pattern.compile("\\s+");
    Matcher matcher2 = replace.matcher(EXAMPLE_TEST);
    System.out.println(matcher2.replaceAll("\t"));
  }


  public static final String EXAMPLE_TEST = "This is my small example "
      + "string which I'm going to " + "use for pattern matching.";

  public static void main4(String[] args) {
    System.out.println(EXAMPLE_TEST.matches("\\w.*"));
    String[] splitString = (EXAMPLE_TEST.split("\\s+"));
    System.out.println(splitString.length);// should be 14
    for (String string : splitString) {
      System.out.println(string);
    }
    // replace all whitespace with tabs
    System.out.println(EXAMPLE_TEST.replaceAll("\\s+", "\t"));
  }

  public static void main(String[] args) throws IOException {

    String otherThanQuote = " [^\"] ";
    String quotedString = String.format(" \" %s* \" ", otherThanQuote);
    String regex = String.format("(?x) " + // enable comments, ignore white spaces
            "<                         " + // match a comma
            "(?=                       " + // start positive look ahead
            "  (?:                     " + //   start non-capturing group 1
            "    %s*                   " + //     match 'otherThanQuote' zero or more times
            "    %s                    " + //     match 'quotedString'
            "  )*                      " + //   end group 1 and repeat it zero or more times
            "  %s*                     " + //   match 'otherThanQuote'
            "  $                       " + // match the end of the string
            ")                         ", // stop positive look ahead
        otherThanQuote, quotedString, otherThanQuote);

    regex = "<|>.+";

    String[] splitString = (LINK.split(regex));
    System.out.println(splitString.length);// should be 14
    for (String string : splitString) {
      System.out.println(string);
    }
    regex = "\"";

    splitString = (LINK.split(regex));
    System.out.println(splitString.length);// should be 14
    for (String string : splitString) {
      System.out.println(string);
    }

    String line = "This order was placed for QT3000! OK?";
    Pattern pattern = Pattern.compile("(.*?)(\\d+)(.*)");
    Matcher matcher = pattern.matcher(line);
    while (matcher.find()) {
      System.out.println("group 1: " + matcher.group(1));
      System.out.println("group 2: " + matcher.group(2));
      System.out.println("group 3: " + matcher.group(3));
    }

    pattern = Pattern.compile("\\s*<(.*?)>\\s*;\\s*rel\\s*=\\s*\"(.*?)\"\\s*");
    matcher = pattern.matcher(LINK);
    while (matcher.find()) {
      System.out.println("group 1: " + matcher.group(1));
      System.out.println("group 2: " + matcher.group(2));
    }

  }

  private void readResponseInputStream(Response response) throws IOException {
    String body = new BufferedReader(
        new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));
  }
}



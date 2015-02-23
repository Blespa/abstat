package it.unimib.disco.summarization.starter;

/*
 * Copyright (c) 2011, Jade Cheng
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Hawaii nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY Jade Cheng ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Jade Cheng BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * This program encodes command-line arguments as a Google search query,
 * downloads the results, and displays the corresponding links as output.
 */
public final class GoogleSearch {

    /**
     * The main entry point of the program.
     * 
     * @param args
     *            The command-line arguments. These arguments are encoded as a
     *            Google search query.
     * @return 
     */
    public static String getFirstParagraphFromWikipediaLink(final String[] args) {
        // Check for usage errors.
        if (args.length == 0) {
            System.out.println("usage: GoogleSearch query ...");
            return "";
        }

        // Catch IO errors that may occur while encoding the query, downloading
        // the results, or parsing the downloaded content.
        try {
            // Encode the command-line arguments as a Google search query.
            final URL url = encodeGoogleQuery(args);

            // Download the content from Google.
            System.out.println("Downloading [" + url + "]...\n");
            final String html = downloadString(url);

            // Parse and display the links.
            final List<URL> links = parseGoogleLinks(html);
            for (final URL link : links){
            	//System.out.println(link);
            	final String html1 = downloadString(link);
            	final String firstPar = parseWikipediaLinks(html1);
            	return firstPar;
                
            	//System.out.println(link + " - " + firstPar);
            }

        } catch (final IOException e) {
            // Display an error if anything fails.
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
		return "";
    }

    /**
     * Reads all contents from an input stream and returns a string from the
     * data.
     * 
     * @param stream
     *            The input stream to read.
     * 
     * @return A string built from the contents of the input stream.
     * 
     * @throws IOException
     *             Thrown if there is an error reading the stream.
     */
    private static String downloadString(final InputStream stream)
            throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while (-1 != (ch = stream.read()))
            out.write(ch);
        return out.toString();
    }

    /**
     * Downloads the contents of a URL as a String. This method alters the
     * User-Agent of the HTTP request header so that Google does not return
     * Error 403 Forbidden.
     * 
     * @param url
     *            The URL to download.
     * 
     * @return The content downloaded from the URL as a string.
     * 
     * @throws IOException
     *             Thrown if there is an error downloading the content.
     */
    private static String downloadString(final URL url) {
        final String agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.28 Safari/537.31";
        URLConnection connection = null;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("No Connection");
			return "";
		}
        connection.setRequestProperty("User-Agent", agent);
        InputStream stream = null;
		try {
			stream = connection.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("No Stream");
			return "";
		}
        
        HttpURLConnection httpConnection = (HttpURLConnection) connection;

        int code = 0;
		try {
			code = httpConnection.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "";
		}
        
        //System.out.println(code);
        
        if(code!=404)
			try {
				return downloadString(stream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return "";
			}
		else
        	return "";
        
    }

    /**
     * Encodes a string of arguments as a URL for a Google search query.
     * 
     * @param args
     *            The array of arguments to pass to Google's search engine.
     * 
     * @return A URL for a Google search query based on the arguments.
     */
    private static URL encodeGoogleQuery(final String[] args) {
        try {
            final StringBuilder localAddress = new StringBuilder();
            localAddress.append("/search?q=");

            for (int i = 0; i < args.length; i++) {
                final String encoding = URLEncoder.encode(args[i], "UTF-8");
                localAddress.append(encoding);
                if (i + 1 < args.length)
                    localAddress.append("+");
            }

            return new URL("http", "www.google.com", localAddress.toString());

        } catch (final IOException e) {
            // Errors should not occur under normal circumstances.
            throw new RuntimeException(
                    "An error occurred while encoding the query arguments.");
        }
    }

    /**
     * Parses HTML output from a Google search and returns a list of
     * corresponding links for the query. The parsing algorithm is crude and may
     * not work if Google changes the output of their results. This method works
     * adequately as of February 28, 2011.
     * 
     * @param html
     *            The HTML output from Google search results.
     * 
     * @return A list of links for the query.
     * 
     * @throws IOException
     *             Thrown if there is an error parsing the results from Google
     *             or if one of the links returned by Google is not a valid URL.
     */
    private static List<URL> parseGoogleLinks(final String html)
            throws IOException {

        // These tokens are adequate for parsing the HTML from Google. First,
        // find a heading-3 element with an "r" class. Then find the next anchor
        // with the desired link. The last token indicates the end of the URL
        // for the link.
    	final String token1 = "<h3 class=\"r\">";
        final String token2 = "<a href=\"";
        final String token3 = "\"";
        
        //System.out.println(html);

        final List<URL> links = new ArrayList<URL>();

        try {
            // Loop until all links are found and parsed. Find each link by
            // finding the beginning and ending index of the tokens defined
            // above.
            int index = 0;
            while (-1 != (index = html.indexOf(token1, index))) {
                final int result = html.indexOf(token2, index);
                final int urlStart = result + token2.length();
                final int urlEnd = html.indexOf(token3, urlStart);
                final String urlText = html.substring(urlStart, urlEnd);
                //System.out.println(urlText.replaceAll("\\<.*?\\>", ""));
                final URL url = new URL(StringEscapeUtils.unescapeHtml4(urlText).replaceAll("\\<.*?\\>", ""));
                links.add(url);

                index = urlEnd + token3.length();
                break;
            }

            return links;

        } catch (final MalformedURLException e) {
            throw new IOException("Failed to parse Google links.");
        } catch (final IndexOutOfBoundsException e) {
            throw new IOException("Failed to parse Google links.");
        }
    }
    
    private static String parseWikipediaLinks(final String html)
            throws IOException {

        // These tokens are adequate for parsing the HTML from Google. First,
        // find a heading-3 element with an "r" class. Then find the next anchor
        // with the desired link. The last token indicates the end of the URL
        // for the link.
        //final String token1 = "<div id=\"mw-content-text\" lang=\"en\" dir=\"ltr\" class=\"mw-content-ltr\">";
        //final String token2 = "<p>";
        //final String token3 = "</p>";

        String links = new String();
        
        Document doc = Jsoup.parse(html);

        Elements resultLinks = doc.select("div#mw-content-text > p"); // direct a after h3
        
        //System.out.println(html);
        
        /*
        List<String> toRem = new ArrayList<String>();
        
        Element resHtml = resultLinks.get(0);
        
        for(Iterator<Node> i = resultLinks.get(0).childNodes().iterator(); i.hasNext();) {
        	Node nd = i.next();

        	boolean toRemove = false;
        	for(Attribute attr: nd.attributes()){
        		if(attr.getKey().equals("class") && attr.getValue().equals("IPA")){
        			toRemove =  true;
        		}
        	}
        	if(toRemove){
        		toRem.add(nd.outerHtml());
        	}
        }
        
        for(String rem : toRem){
        	resHtml.outerHtml().replaceFirst(rem, "");
        }
        */
        //System.out.println(html);
        try{
        	String resLink = resultLinks.get(0).html();

        	resLink = resLink.replaceAll("\\[citation needed\\]", ""); //[citation needed]

        	resLink = resLink.replaceAll("\\[[0-9]+\\]", "");//[7]

        	resLink = resLink.replaceAll("\\(<span.+</i>\\)", ""); //Replax test like http://en.wikipedia.org/wiki/Archaea first ()

        	Document doc1 = Jsoup.parse(resLink);

        	links = doc1.text(); //.replaceAll("\\(.+\\)", "")

        	return links;
        }
        catch(IndexOutOfBoundsException e){
        	return "";
        }
        
        /*
        try {
            // Loop until all links are found and parsed. Find each link by
            // finding the beginning and ending index of the tokens defined
            // above.
            int index = 0;
            while (-1 != (index = html.indexOf(token1, index))) {
                final int result = html.indexOf(token2, index);
                final int urlStart = result + token2.length();
                final int urlEnd = html.indexOf(token3, urlStart);
                final String urlText = html.substring(urlStart, urlEnd);
                //System.out.println(urlText.replaceAll("\\<.*?\\>", ""));
                //final URL url = new URL("http://" + urlText.replaceAll("\\<.*?\\>", ""));
                links=urlText.replaceAll("\\<.*?\\>", "");

                index = urlEnd + token3.length();
                break;
            }

            return links;
           
        } catch (final IndexOutOfBoundsException e) {
            throw new IOException("Failed to parse Google links.");
        }
        */
    }
}
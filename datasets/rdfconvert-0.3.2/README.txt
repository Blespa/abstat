RDFConvert - a simple command-line tool for RDF syntax conversion

Usage: bin/rdfconvert [OPTIONS] <sourcefile> [<destinationfile>]
 -h,--help           Print this help
 -i,--input <arg>    The RDF syntax format of the input file. Valid values are
                     'RDF/XML', 'N-Quads', 'N-Triples', 'Turtle', 'TriG',
                     'TriX', 'RDF/JSON' and 'BinaryRDF'. If not specified, the input syntax
                     format will be automatically determined based on the
                     extension.
 -o,--output <arg>   The RDF syntax format to which the file should be
                     converted. Valid values are 'RDF/XML' (the default),
                     'N-Quads', 'N-Triples', 'N3', 'Turtle', 'TriG', 'TriX', 'RDF/JSON' and
                     'BinaryRDF'.
                     
If <destinationfile> is not specified, the output will be sent to STDOUT.

This software is copyright Rivuli Development (http://www.rivuli-development.com/) (c) 2011-2014.
Developed by Jeen Broekstra (jeen@rivuli-development.com).

Licensed under a BSD style license. For the full text of the license, see the
file LICENSE.txt. By using, modifying or distributing this software you agree to be bound by the
terms of this license.

Many thanks to Joshua Shinavier (Sesame Tools/N-Quads parser) and 
OpenRDF Sesame (OpenRDF Rio Toolkit).

This software includes 3rd-party components that are licensed under different
conditions. For details see the file NOTICE.txt.
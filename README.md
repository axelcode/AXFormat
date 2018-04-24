# AXFormat
A Java library to parse data streams

The recognized formats are:

1. Json
2. Csv

# JSON

The library allows you to read and get the encoding of a json file in a simple and fast way.

Once you have read the file, to access the individual fields, there are three methods:

public String getValue(String key)
this method allows to obtain the value in string format by specifying the search key.
The key has this format:

<field name> > <field name> > ...


the x tag allows to separate the fields inside the json's tree.

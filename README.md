# AXFormat
A Java library to parse data streams

The recognized formats are:

1. Json
2. Csv

## JSON

The library allows you to read and get the encoding of a json file in a simple and fast way.

Once you have read the file, to access the individual fields, there are three methods:

**public String getValue(String key)**
This method allows to obtain the value in string format by specifying the search key.

**public String[] getArray(String key)**
This method allows to obtain an array of values ​​in case the json node is an array.

**public AXJsonValue getField(String key)**
This method allows to obtain an AXJsonValue object which represents the value of the json node with its properties.

## Search key
The search key has this format:

```bash
<field name> > <field name> > ...
```

the **>** tag allows to separate the fields inside the json's tree.

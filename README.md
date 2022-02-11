# OpenConvert


The OpenConvert tools output TEI from a number of input formats. 

## Using the command line

The OpenConvert distribution can be accessed at https://github.com/INL/OpenConvert.

The command line can be used as follows:

    java -jar OpenConvert.jar -from <input_format> -to <output_format> <input> <output>
 
Options:

- `-from` 	input format: text, TEI, alto, doc, docx, HTML

- `-to`	output format: TEI, text or folia

Arguments:

- `input`	filename, directory name or zip archive name (ending with .zip)

- `output` filename, directory name or zip archive name (ending with .zip)


If the from and to flags are omitted, the conversion to be applied will be guessed from file name extensions. 

 
**NOTE**: the default setting for server is a bit unfortunately set to an INT-internal address.
You should be able to run your own test run with the following command line:

    java -jar openconvert.client.jar -f text -t tei -a chn-tagger -s https://openconvert.ivdnt.org/openconvert/file test.txt
 

# OpenConvert


The OpenConvert tools output TEI from a number of input formats. 

Using the command line

The OpenConvert distribution can be accessed at https://github.com/INL/OpenConvert.

The command line can be used as follows:

 java -jar OpenConvert.jar -from <input format> -to <output format> <input> <output>
 
Options:

-from 	input format: text, TEI, alto, doc, docx, HTML

-to	output format: TEI, text or folia

Arguments:

input	filename, directory name or zip archive name (ending with .zip)

output	filename, directory name or zip archive name (ending with .zip)


If the from and to flags are omitted, the conversion to be applied will be guessed from file name extensions. 

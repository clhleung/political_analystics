# political_analystics

This is code for the data analytics challenge written in the Java language. 

## To run

To run this program, just run the included run.sh script (by running "./run.sh" after turning on the execute bit using chmod).

## Assumptions

# Varchar Type
In the election website, many of the required parameters specified by the document were usually some form of varchar type.
I took that to mean that I am expected to parse through data that could contain any type of input that can be typed on a standard 
computer keyboard. To expect these type of inputs I looked up the Ascii value table on this website 

http://ee.hawaii.edu/~tep/EE160/Book/chap4/subsection2.1.1.1.html

and included every printable character starting from "!" using a Regex string (minus the "|" which is needed for parsing the row column values) all the way to the ascii value of "~". I assume for the most part that when a data field has the varchar parameter, it DOES NOT include hidden characters defined within the ASCII table, since those normally can't be typed normally on a standard keyboard. 

# Handle inordered repeat donor transactions
I also took it to mean that if given a record that has a transaction date that is before a previous date from a transaction by the same repeat donor, we simply ignore that record altogether and continue parsing the itcont.txt file. For example given say, the file contents

C00384516|N|M2|P|201702039042410894|15|IND|SABOURIN, JOE|LOOKOUT MOUNTAIN|GA|028956146|UNUM|SVP, CORPORATE COMMUNICATIONS|01312016|484||PR2283904845050|1147350||P/R DEDUCTION ($192.00 BI-WEEKLY)|4020820171370029339

C00384516|N|M2|P|201702039042410894|15|IND|SABOURIN, JOE|LOOKOUT MOUNTAIN|GA|028956146|UNUM|SVP, CORPORATE COMMUNICATIONS|01312015|384||PR2283904845050|1147350||P/R DEDUCTION ($192.00 BI-WEEKLY)|4020820171370029339

C00384516|N|M2|P|201702039042410893|15|IND|SABOURIN, JOE|LOOKOUT MOUNTAIN|GA|028956146|UNUM|SVP, CORPORATE COMMUNICATIONS|01312017|230||PR1890575345050|1147350||P/R DEDUCTION ($115.00 BI-WEEKLY)|4020820171370029335

I would ignore the middle transaction made in 2015 since that is lower than 2016, and likewise, any valid transaction that comes 
from the same repeat donor and to the same candidate with any date lower than 2017 in the third record will also be ignored until 
a later transaction date appears on a valid record. 

## Approach
When I first saw this assignment on Monday (got an extension due to handling 2 midterms on Sunday 2/11, due 2/16 9am PST for me), the first thing that came to my mind was Regex Strings, since a major component of this assignment was to parse huge amounts of data. So, the one of the first few things I did was to play around with Regex Strings, and create a Regex String that is capable of eliminating 
most of the invalid rows out there, including those with:

Improper number of chracters in the interested fields we are interested in

Fields that should be left blank but are filled out (OTHER_ID for example)

To create this Regex String this is capable of parsing a row record found in a typical itcont.txt file, I used the website
https://regexr.com/
which allowed me to play around with my Regex String & see in real time what my string actually did. In addition to weeding out 
some of these invalid rows, I also had to create another Regex String to weed out invalid dates, since that could not be achieved 
through something like "[0-9] {8}" which only checks if the date field has 8 characters or not. So, I also had to spend time creating
a Regex String that can account for valid days in a month, since some months have fewer days than other months. Although there are 
other ways to parse through data, I chose to learn and use Regex String, since I'm required to parse through huge blocks of data, and 
I also felt that using other parsing methods could lead to more code, which can convulate the program and lead to slower exectution
times, as well as reduce readibility and increase debugging time. 

Asides from thinking about Regex Strings, the other few things that popped up from my head was to handle the basic file I/O stuff, and have separate functions for input and output, since that was important to read the input files and print out the results to the
specified output file (& it would look messy to stuff it all inside the main function). I also took inspiration from the S in the Solid 
software design principle, which stated that a class should be designed to do 1 thing thing only. In this case, I created a Record class
solely to take a Record string from itcont.txt and convert it to a Record class so that it would be easier for me to get back to when
I needed to retrieve a certain piece of data from it (like name & zip), rather than repeatably write out the whole code from by scratch
in 1 file. 

After working out the basic file i/o details and the Record class to handle data retrieval for the fields we're interested in, 
the biggest challenge that laid ahead was to actually create the logic that will print out the rows we need to repeat-donors.txt
To the best of my ability, I tried to comment out my code to explain what I intended to do. Obviously, we are forced to keep track
of a large amount things whenever we attempt to create a row. We have to keep track of

donors who are repeat donors, 

the last most recent date a repeat donor had give money, 

the donations a candidate receives in a particular year from repeat donors, 

the number of unique donors that gave money to the same candidate in the same year (there could be cases where 1 donor gave $$ more than once in the same yr). 

The above cases were all equally important in determining the data we need to print out, and I used a Java HashMap for each of these
cases, since it can map a key to a particular value, which was needed to keep track of all this information. After getting the hashmaps
set up, I simply iterated through each record given, and added data to the various hashmaps, while consistently checking for conditons
like repeat donors or valid dates that are greater than the last dates for a particular donor. When it came to constructing a row,
I referenced data already stored in the hashmaps and constructed a string to insert into an arraylist, where its contents will be 
writen to the output file. 

## Testing

In terms of testing, I created my own input files and changed some values around to create conditions where the records would not be
read at all (like invalid dates, length of desired field too short). I also created test files where there were multiple repeat donors, 
and conditions where donors donate to the same candidate in different years. Obviously there are many tests that can be made up and I
clearly can't cover all of them, but I tried to cover the corner cases and the ones that obviously stood out the most to me when taking 
into consideration the ways a person can mess up writing this campaign file. 

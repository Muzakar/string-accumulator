# Tech stack
- Java 1.8
- Maven

# Java libraries
- Apache Commons lang
- JUnit 4

# Thought process while finalizing the approach
I was able to think of 3 ways to come up with a solution
1. Regex :- 
There could be two compiled patterns to extract the delimiters and a dynamically compiled pattern which could retrieve the numbers from the string.
Retrieved numbers can be iterated to get the sum, identify the negative number and the numbers greater than 1000 
Downside to this approach is the dynamically compiled pattern which is an expensive operation.

2. Recursion :- 
The above approach can be used to extract the delimiters and rather than having dynamically compiled patterns, recursion can be used to sum the numbers whilst extracting.
However, it would be difficult and more complex logic to supply patterns to substring during recursion.

3. Replacing all the delimiters with a temporary delimiter :-
Static patterns can be used to extract the delimiters.
These delimiters can be iterated and replaced with a temporary delimiter in the string having numbers and other characters.
Then, the updated string can be split based on temporary delimiter to get an array numbers and can be accumulated accordingly.

To me, the third approach looks better. So, implemented the same.
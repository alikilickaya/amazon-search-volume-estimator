# SVEstimator (Search Volume Estimator)

Estimates the search volume of a keyword using Amazon's autocomplete API

## How does it work?

It is calling the autocomplete API for keyword itself and its substrings.
And if the result contains the keyword add a score to the estimation.

Each call, for each substrings, have different scores.
As the possibility to see the keyword in the response when we call the API with the keyword itself 
is much more than when we call with the first character of the keyword.

So, the algorithm calculates units for each call. The call with keyword itself is counted as 1 unit, and
the other substrings are multiply base unit by powers of 'EXPONENTIAL_FACTOR'. 'EXPONENTIAL_FACTOR' is set as 1.25

For example: keyword is "nike", 'EXPONENTIAL_FACTOR' is 2 in the examples.
call for˙
"nike" is 2^0 = 1 unit
"nik" is 2^1 = 2 units
"ni" is 2^2 = 4 units
"n" is 2^3 = 8 units

The total number of units is 15. 
the score of an unit -> Max score / total number of units.
in this case; 100 / 15 = 6,66

it checks whether the response contains the keyword. If so summing it up for the estimation.

* First, the algorithm is checking corner cases which are calling API with the keyword itself 
and calling API with the first character of the keyword.
    * if the response doesn't contain the keyword when the API is called with keyword itself,
    algorithm returns 0. Because if it is not in the response for even called with its all characters,
    it means it is not a famous keyword and no need to call with its substrings.
    * if the response contains the keyword when the API is called with the first character of the
    keyword, algorithm returns 100. Because if it is in the response when the API is called with only 
    one character, it means it is one of the hottest keywords. When you consider, between how many words
    which start with that character, the keyword is on the first 10 words.
    
* if these both corner cases are not the case, it continues to call the API with sub strings. 

All steps for the keyword "nike"
call for "nike" -> result contains the keyword -> 1 unit * the score of an unit = 1 * 6,66 = 6.66
call for "n" -> result does not contain keyword -> 0
call for "nik" -> result contains the keyword -> 2 units * the score of an unit = 2 * 6,66 = 13.32
call for "ni" -> result contains the keyword -> 4 units * the score of an unit = 2 * 6,66 = 26,64

estimation score = 6.66 + 0 + 13.32  + 26.64 = 46,62


* P.S EXPONENTIAL_FACTOR is 2 in all examples given above.
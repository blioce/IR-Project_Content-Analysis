# IR Project_Content Analysis
This project will be the starting point for analyzing lyrics for potentially harmful content such as violence and substance abuse. We will begin by using the data in the Million Songs Dataset (MSD) which is provided in the files under /src. This file is implemented as a bag of words using the top 5,000 words used across the whole dataset which includes more than 230,000 tracks. 

# TF*IDF
To begin the analysis, we will start by using term frequency (tf) and inverse document frequency (idf) to gather data on the significance of the words in the lyrics. To implement the idf, we need to know the number of documents in the collection and the number of documents that each words appears in. 
  - For the first (train) dataset file, there are <b>210,519 tracks</b>.
  - Under the MSD_TFIDF folder, there is a file that I created that has additional word information called <b>Word Info.txt</b> in the following format:
    - The word itself, followed by the frequency of this word (across all tracks), and the number of tracks this word appears in.
    - For example: a line in the file will show "wreck 1879 1282"

# To Import the Project into Eclipse...
Import the MSD_TFIDF project folder as a Maven project. By doing this as a Maven project rather than a standard Java project, we can easily import and implement other libraries and tools in the code. 

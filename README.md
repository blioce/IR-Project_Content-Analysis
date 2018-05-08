# IR Project_Content Analysis
This project will be the starting point for analyzing lyrics for potentially harmful content such as violence and substance abuse. We will begin by using the data in the Million Songs Dataset (MSD) which is provided in the files under /src. This file is implemented as a bag of words using the top 5,000 words used across the whole dataset which includes more than 230,000 tracks. 

# TF*IDF
<b>A file has been created using all the words and all tracks in the train dataset file. The format is of the following: trackID,wordIndex:tf\*idf,wordIndex:tf\*idf,... (essentially the same format as the dataset file itself).
  
  Due to the size of this file, visit this <a href="https://drive.google.com/file/d/1H3liP8iQI3SyuacBWzBOgMIsABEAR8cO/view?usp=sharing">link</a> on Google Drive to download. It is too large for github. By removing stopwords from the bag-of-words, we can greatly reduce the size of this file.</b>
  
To begin the analysis, we will start by using term frequency (tf) and inverse document frequency (idf) to gather data on the significance of the words in the lyrics. To implement the idf, we need to know the number of documents in the collection and the number of documents that each words appears in. 
  - For the first (train) dataset file, there are <b>210,519 tracks</b>.
  - Under the MSD_TFIDF folder, there is a file that I created that has additional word information called <b>Word Info.txt</b> in the following format:
    - The word itself followed by the number of tracks this word appears in.
    - For example: a line in the file will show "wreck 1282"

# To Import the Project into Eclipse...
Import the MSD_TFIDF project folder as a Maven project. By doing this as a Maven project rather than a standard Java project, we can easily import and implement other libraries and tools in the code. 

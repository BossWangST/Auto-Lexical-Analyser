# Auto-Lexical-Analyser
2022 NNU Spring Compilers Mini-C Project (Lexical Analyser part)
# How to use?
## Step 1
Clone this repository into your own pc.

(Optional)Create an IDEA project with the folder.

e.g:

`git clone https://github.com/BossWangST/Auto-Lexical-Analyser.git`

## Step 2
You need to write a `lex.txt` in the `/src/main/java` folder in advance, this file should be like the one in this repository, which explains the rules of your own language.

In the IDEA project(or in the `/src/main/java` folder), compile the `Lex.java` and you will get a `.java` file called `Lexical_Analysis.java`

e.g.:

`javac Lex.java`

Tip:Here the `Lexical_Analysis.java` will appear in the `/lex_test/src/main/java`.

## Step 3
Compile `Lexical_Analysis.java` and run it, it will give you a sequence of Tokens.

## FAQ
If your OS is Windows, you need to change the file path format to something like `\\lex_text\\src\\main\\java`
then you can compile and run the Lexical Analyser.
# data-structs-tp2

## Compiling the code

```bash
make
```

## Running the code

```bash
java com.ift2015.tp2.NetworkApp TestCase1.txt
```

or

```bash
make run ARGS="TestCase2.txt --verbose"
```

## Results

- The resulting json will be created with the filename Output_{Filename}.json.
- It is assumed that the input file is a .txt file.
- It is assumed that the filepath is flat, meaning that the input file is in the same directory as the compiled code.

For example if the input file is `TestCase1.txt`, the output file will be `Output_TestCase1.json`.

## Notes

The test case of the pdf is `TestCasePDF.txt`, the City C was modified to have demand of 50 instead of 20. Otherwise the example output in the pdf does not make sense.

## Authors

- Yorguin Jose Mantilla Ramos
- Tikshan Soobanah
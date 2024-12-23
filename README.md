# IFT2015 - TP2: Emergency Supply Network

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
- It is assumed that no negative values are present in the input file.

For example if the input file is `TestCase1.txt`, the output file will be `Output_TestCase1.json`.

## Notes

The test case of the pdf is `TestCasePDF.txt`, the City C was modified to have demand of 50 instead of 20. Otherwise the example output in the pdf does not make sense.

The test case `TestCasePDFOverDemand.txt` is the same as `TestCasePDF.txt` but with the demand of City A increased to 1500 to study what happens when the demand is higher than the supply. Indeed, all the supply is sent to City A and the other cities are left without supply, as city A has higher priority than the others.

## Authors

- Yorguin Jose Mantilla Ramos
- Tikshan Soobanah

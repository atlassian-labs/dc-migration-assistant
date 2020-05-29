### What

Generate a parameters file that is used to render the quickstart form in the migration assistant plugin UI.

### How

Run the python file to print the parameters file.

```bash
python parameters_gen.py --template /path/to/quickstart-jira-dc.template.yaml > quickstart-jira-dc.template.parameters.yaml 

```

### What's next? 

Upload the file to the S3 bucket. Don't forget to allow `cors` on the object

### Improvements

Script the S3 upload process using `make`
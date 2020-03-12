packagesource:
	rm package/MigrationHelper.zip; cd src/MigrationHelper/; zip -r ../../package/MigrationHelper.zip .; cd ../..
shipsource:
	aws s3 cp package/MigrationHelper.zip s3://$(BUCKET)
createstack:
	aws cloudformation create-stack --stack-name migration-helper --template-body file://migration-helper.yml --capabilities CAPABILITY_IAM --disable-rollback
deletestack:
	aws cloudformation delete-stack --stack-name migration-helper
updatestack:
	aws cloudformation update-stack --stack-name migration-helper --template-body file://migration-helper.yml --capabilities CAPABILITY_IAM
STACK_NAME := $(shell whoami)-$(shell date +"%d-%mt%H-%M-%S")-migration-helper
INSTANCE_TYPE ?= c5.large

# Params for create-stack
# VPC_ID: the VPC to deploy the elastic beanstalk environment in
# SUBNET_ID: the IDs of the subnets to distribute the elastic beanstalk EC2 instance between (There is only 1 instance so should just be a single subnet)
# EFS_ID: the file system ID of the EFS to sync the files to. Should be the EFS of the freshly deployed product stack
# SG_ID: The ID of the security group attached to the EFS, required to allow the beanstalk instance to communicate with the EFS for the duration of the migration

PARAM_VPC := ParameterKey=HelperVpcId,ParameterValue=$(VPC_ID)
PARAM_SUBNET := ParameterKey=NetworkPrivateSubnet,ParameterValue=$(SUBNET_ID)
PARAM_EFS := ParameterKey=EFSFileSystemId,ParameterValue=$(EFS_ID)
PARAM_EFS_SG := ParameterKey=EFSSecurityGroup,ParameterValue=$(SG_ID)
PARAM_INSTANCE_TYPE := ParameterKey=HelperInstanceType,ParameterValue=$(INSTANCE_TYPE)
PARAM_RDS_SG := ParameterKey=RDSSecurityGroup,ParameterValue=$(SG_ID)

PARAMS := $(PARAM_VPC) $(PARAM_SUBNET) $(PARAM_EFS) $(PARAM_EFS_SG) $(PARAM_INSTANCE_TYPE) $(PARAM_RDS_SG)

rendertemplate:
	mkdir render;
	echo "/%{HOME_COPY_SCRIPT}%/{\n\ts/%{HOME_COPY_SCRIPT}%//g\n\tr render/tab_sync.py\n}" > render/sed
	sed -E "s/^/                    /g" pkg/parse_sync_output.py > render/tab_sync.py;
	sed -f render/sed pkg/migration-helper.yml.src > migration-helper.yml
	rm -rf render
createstack:
	aws cloudformation create-stack --stack-name $(STACK_NAME) --template-body file://migration-helper.yml --parameters $(PARAMS) --capabilities CAPABILITY_IAM --disable-rollback
deletestack:
	aws cloudformation delete-stack --stack-name $(STACK_NAME)
updatestack:
	aws cloudformation update-stack --stack-name $(STACK_NAME) --template-body file://migration-helper.yml --parameters $(PARAMS) --capabilities CAPABILITY_IAM


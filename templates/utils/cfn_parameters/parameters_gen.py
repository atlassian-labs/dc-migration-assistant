#!/usr/bin/python3

import argparse
from cfn_tools import load_yaml, dump_yaml


class ParametersGen:
    def __init__(self):
        pass

    def generate(self, template_path):
        with open(template_path, 'r') as template:
            cfn_yaml = load_yaml(template.read())

            parameter_groups = cfn_yaml['Metadata']['AWS::CloudFormation::Interface']['ParameterGroups']
            parameter_labels = cfn_yaml['Metadata']['AWS::CloudFormation::Interface']['ParameterLabels']
            parameters = cfn_yaml['Parameters']

            print(dump_yaml({
                "ParameterGroups": parameter_groups,
                "ParameterLabels": parameter_labels,
                "Parameters": parameters
            }))


def main(template_path):
    ParametersGen().generate(template_path)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='default parser')
    parser.add_argument('--template', metavar='path', required=True,
                        help='path to template')
    args = parser.parse_args()

    main(template_path=args.template)

# Contributing to Atlassian DC Migration Assistant

Thank you for considering a contribution to DC Migration Assistant! Pull requests, issues and comments are welcome. For pull requests, please:

* Use the [feature branching](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow) pattern
    * If you have changes that are dependent on another developer's branch, create your feature branch off their branch. Your PR should also target their branch.
* Pull requests should target the branch you branched off (master is the default branch)
* Try to keep one PR scoped to one issue
* Pull requests require at least one approval from a maintainer and all builds must pass
* Add tests for new features and bug fixes
* Follow the existing style
* Separate unrelated changes into multiple pull requests

See the existing issues for things to start contributing.

For bigger changes, please make sure you start a discussion first by creating an issue and explaining the intended change.

Atlassian requires contributors to sign a Contributor License Agreement, known as a CLA. This serves as a record stating that the contributor is entitled to contribute the code/documentation/translation to the project and is willing to have it used in distributions and derivative works (or is willing to transfer ownership).

Prior to accepting your contributions we ask that you please follow the appropriate link below to digitally sign the CLA. The Corporate CLA is for those who are contributing as a member of an organization and the individual CLA is for those contributing as an individual.

* [CLA for corporate contributors](https://na2.docusign.net/Member/PowerFormSigning.aspx?PowerFormId=e1c17c66-ca4d-4aab-a953-2c231af4a20b)
* [CLA for individuals](https://na2.docusign.net/Member/PowerFormSigning.aspx?PowerFormId=3f94fbdc-2fbe-46ac-b14c-5d152700ae5d)

# Intellij Idea settings

## Copyright
This project is distributed under the Apache v2 license. Intellij provides a tool to automatically add copyright headers to all files. To enable this:
1. Go to Settings -> Editor -> Copyright -> Copyright Profiles.
1. Add a new profile with the name `Atlassian Apache 2`
1. Add the copyright text (see below); the year setting should be `$today.year`.
1. Go up one setting level to Copyright and add a scope of `All` with this new profile.

The menu option `Update copyright` can be used from the file-tree context menu.

###  Copyright text

```
Copyright $today.year Atlassian
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
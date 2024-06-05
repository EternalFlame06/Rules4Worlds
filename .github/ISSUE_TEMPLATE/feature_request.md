name: Feature request
description: "For requesting new features"
labels:
  - enhancement
body:
  - type: markdown
    attributes:
      value: >-
        Before proceeding, please consider the following:
        
        - Have you checked if the mod is on the latest version? The latest version can be found [here](https://modrinth.com/mod/warputils/versions)
        
        - Please also check if the feature has already been requested
        
        - Please only request one feature per issue, unless they are closely related

        
  - type: textarea
    id: description
    attributes:
      label: Feature Description
      description: >-
        Describe the feature you want to be added here. If it is related to a problem, you can also describe that here.

        **Hint:** If you have any additional information, such as screenshots or videos, that you feel would help explain the issue, please attach them here.
    validations:
      required: true
  
  - type: textarea
    id: altDescribtion
    attributes:
      label: Alternative Description
      description: >-
        If you have thought of any alternatives, feel free to describe them here.

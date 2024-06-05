name: Bug Report
description: "For reporting bugs"
labels:
  - bug
body:
  - type: markdown
    attributes:
      value: >-
        Before proceeding, please consider the following:
        
        - Have you checked if the mod is on the latest version? The latest version can be found [here](https://stackoverflow.com/questions/64373516/github-actions-you-have-an-error-in-your-yaml-syntax).
        
        - Please also check if the issue has already been reported.

        
  - type: textarea
    id: description
    attributes:
      label: Bug Description
      description: >-
        Describe the issue in as much detail as possible. This section should contain what behavior you expect and why you believe the actual behavior is a bug. If this issue only occurs with specific mods installed, provide their names and versions.

        **Hint:** If you have any additional information, such as screenshots or videos, that you feel would help explain the issue, please attach them here.
    validations:
      required: true
  
  - type: textarea
    id: description-reproduction-steps
    attributes:
      label: Reproduction Steps
      description: >-
        Please provide detailed instructions on how to reproduce the bug. Make sure your instructions are clear and concise, as other people will need to follow them to recreate the issue.
        
        **Hint:** A common way to fill this section out is to write a step-by-step guide.
    validations:
      required: true
      
  - type: textarea
    id: log-file
    attributes:
      label: Log File
      description: >-
        **Hint:** You can usually find the log files within the folder `.minecraft/logs`. Most often, you will want the `latest.log`
        file, since that file belongs to the last played session of the game.
      placeholder: >-
        Drag-and-drop the log file here.
    validations:
      required: true

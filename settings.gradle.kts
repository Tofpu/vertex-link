rootProject.name = "vertex-link"
include("central-server")
include("edge-node")
include("common")
include("examples")
include("examples:temperature-project")
include("examples:temperature-project:central-server")
include("examples:temperature-project:edge-node")
include("examples:temperature-project:common")

project(":examples:temperature-project").children.forEach { child -> child.name = "temperature-project-${child.name}" }
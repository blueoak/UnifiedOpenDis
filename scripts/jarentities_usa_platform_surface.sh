#!/bin/sh
cd "${0%/*}"
cd ../
jar cf jars/entities_usa_platform_surface.jar -C target/classes edu/nps/moves/dis/entities/usa/platform/surface
jar cf jars/entities_usa_src_platform_surface.jar -C generated/dis7/sisoEntities/java edu/nps/moves/dis/entities/usa/platform/surface

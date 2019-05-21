#!/bin/sh
cd "${0%/*}"
cd ../
jar cf jars/entities_chn.jar -C target/classes edu/nps/moves/dis/entities/chn
jar cf jars/entities_chn_src.jar -C generated/dis7/sisoEntities/java edu/nps/moves/dis/entities/chn

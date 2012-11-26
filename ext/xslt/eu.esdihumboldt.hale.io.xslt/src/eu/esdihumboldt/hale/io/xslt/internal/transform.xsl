<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
#foreach ($prefix_ns in $additionalNamespaces.entrySet())
	xmlns:${prefix_ns.Key}="${prefix_ns.Value}"
#end
	version="2.0"
	xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	
	<!-- Root template -->
	<xsl:template match="/" >
		#foreach ($target in $targets)
		<xsl:variable name="${target}">
			<!-- Transform objects into temporary document -->
			<xsl:call-template name="${target}" />
		</xsl:variable>
		#end
		<!-- Organize transformed objects in container -->
		#include("container.xsl")
	</xsl:template>
	
	<!-- Relation templates -->
#foreach ($include in $includes)
	#include($include)
	
#end
</xsl:transform>
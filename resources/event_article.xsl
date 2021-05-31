<?xml version="1.0"?>

<!-- article formatting for flyer article editor-->
<!-- displays elements for viewing or editing depending on the active attribute -->
<!-- see flyer dtd for details -->
<!-- FORK unstable media ost, schmidt@fork.de -->

<!--<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" indent-result="no" default-space="strip">-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="iso-8859-1" indent="no"/>

<!-- i t e m  -->

  <xsl:template match="item">
    <table cellspacing="2" cellpadding="2" border="0" width="400">
    	<xsl:apply-templates/>
    </table>
  </xsl:template>

<!-- h e a d i n g -->

  <xsl:template match="heading">
    <tr>
      <td bgcolor="#e5e5e5" valign="top" class="copy12">
      	<xsl:apply-templates/>
      </td>
  </tr>
  </xsl:template>

<!-- s u b h e a d i n g -->

  <xsl:template match="subheading">
    <tr>
      <td bgcolor="#e5e5e5" class="copy11">
        <xsl:apply-templates/>
      </td>
 		</tr>
	</xsl:template>

<!-- p -->

  <xsl:template match="p">
    <tr>
      <td bgcolor="#e5e5e5" class="copy10">
     		<xsl:apply-templates/>
     	</td>
 		</tr>
  </xsl:template>

<!-- l i / p -->

  <xsl:template match="list/li/p">
    <xsl:apply-templates />
  </xsl:template>

<!-- l i s t -->

  <xsl:template match="list">
    <table cellspacing="0" cellpadding="0" border="0">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

<!-- l i -->

  <xsl:template match="li">
    <tr>
      <td valign="top">-</td>
      <td width="430" class="copy10">
        <xsl:apply-templates select="child::p"/>
      </td>
    </tr>
	</xsl:template>

<!-- u r l  R e f -->

	<xsl:template match="urlRef">
    <xsl:value-of select="text()"/>
    <xsl:apply-templates select="url"/>
  </xsl:template>

<!-- u r l -->

  <xsl:template match="url">
		<xsl:text> [</xsl:text>
		<xsl:value-of select="@protocol"/>
		<xsl:choose>
			<xsl:when test="@protocol='mail'">
   			<xsl:text>:</xsl:text>
      </xsl:when>
			<xsl:otherwise>
				<xsl:text>://</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="."/>
   	<xsl:text>]</xsl:text>
  </xsl:template>

  <!-- p l a i n -->

  <xsl:template match="plain">
  	<xsl:apply-templates/>
  </xsl:template>

<!-- b o l d -->

  <xsl:template match="b">
  	<b>
  		<xsl:apply-templates/>
  	</b>
  </xsl:template>


  <xsl:template match="i">
    <i>
      <xsl:apply-templates/>
    </i>
  </xsl:template>

  <xsl:template match="footnote">
    <font size="-1">
      <xsl:apply-templates/>
    </font>
  </xsl:template>

</xsl:stylesheet>

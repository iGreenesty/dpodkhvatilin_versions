def ATTRS_FOR_UPDATE_ON_FORMS = ['template']
if (form == null)
{
	return ATTRS_FOR_UPDATE_ON_FORMS
}

if(form.template == null)
{
  return form.text
}
if(form.template != null)
{
  return form.template.text
}
return form.text
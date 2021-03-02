def ATTRS_FOR_UPDATE_ON_FORMS = ['templateTask']
if (form == null)
{
	return ATTRS_FOR_UPDATE_ON_FORMS
}

if (subject == null)
{
	if(form?.templateTask)
  	{
  		return form.templateTask.description
	}
} 
return form.description
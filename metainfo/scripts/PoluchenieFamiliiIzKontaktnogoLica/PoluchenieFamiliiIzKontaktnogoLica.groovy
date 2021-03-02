if(subject.clientName)
{
	def splitted = subject.clientName.split('\\s')
	if(splitted.size() > 0)
		return splitted[0]
}
return ''
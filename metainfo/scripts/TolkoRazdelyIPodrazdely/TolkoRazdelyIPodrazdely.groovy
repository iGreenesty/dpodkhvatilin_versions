def query = "SELECT 'KB\$' || id FROM KB WHERE case_id in ('KBSectionOpen', 'KBSubsectionOp', 'KBSectionProt', 'KBSubsectionPr')"
def list = api.db.query(query).list()
return list
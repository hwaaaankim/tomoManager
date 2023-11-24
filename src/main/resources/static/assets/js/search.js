$(function(){
	if(!searchType || searchType === 'username' || searchType == 'none' || searchType == 'email'){
		$('#periodSearch').hide();
	}else if(searchType==='period'){
		$('#textSearch').hide();
	}
	$('#searchType').on('change',function(){
		if($('#searchType option:selected').attr('id')==='searchName' || 
		$('#searchType option:selected').attr('id') === 'searchPhone' ||
		$('#searchType option:selected').attr('id') === 'searchEmail' ||
		$('#searchType option:selected').attr('id') === 'searchBasic'){
			$('#periodSearch').hide();
			$('#businessSearch').hide();
			$('#textSearch').show();
			$('#textSearch input').val('');
		}else if($('#searchType option:selected').attr('id')==='searchBusiness'){
			$('#textSearch').hide();
			$('#periodSearch').hide();
			$('#businessSearch').show();
		}else if($('#searchType option:selected').attr('id')==='searchPeriod'){
			$('#businessSearch').hide();
			$('#textSearch').hide();
			$('#periodSearch').show();
			$('#startDate').val(new Date().toISOString().slice(0, 10));
			$('#endDate').val(new Date().toISOString().slice(0, 10));
		}
	});
	
});
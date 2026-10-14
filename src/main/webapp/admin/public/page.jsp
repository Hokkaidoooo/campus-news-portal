<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="row">
	<div class="col-xs-6">
		<div class="dataTables_info" id="sample-table-2_info" role="status" aria-live="polite">
			总数:${pageutil.x_rowCount}条	当前第${pageutil.x_pageNow }/${pageutil.x_pageCount }页
		</div>
	</div>
	<div class="col-xs-6">
		<div class="dataTables_paginate paging_simple_numbers" id="sample-table-2_paginate">
			<ul class="pagination">
				<li class="paginate_button next" aria-controls="sample-table-2" tabindex="0" id="sample-table-2_next"><a href="javascript:fyjz(1)">首页</a></li>
				<c:if test="${pageutil.hasPreviousPage()}">
					<li class="paginate_button previous" aria-controls="sample-table-2" tabindex="0" id="sample-table-2_previous"><a href="javascript:fyjz(${pageutil.getPreviousPage() })">上一页</a></li>
				</c:if>
				<c:if test="${!pageutil.hasPreviousPage()}">
					<li class="paginate_button previous disabled" aria-controls="sample-table-2" tabindex="0" id="sample-table-2_previous"><a>上一页</a></li>
				</c:if>
				<c:if test="${pageutil.hasNextPage()}">
					<li class="paginate_button next" aria-controls="sample-table-2" tabindex="0" id="sample-table-2_next"><a href="javascript:fyjz(${pageutil.getNextPage() })">下一页</a></li>
				</c:if>
				<c:if test="${!pageutil.hasNextPage()}">
					<li class="paginate_button next disabled" aria-controls="sample-table-2" tabindex="0" id="sample-table-2_next"><a>下一页</a></li>
				</c:if>
				<li class="paginate_button next" aria-controls="sample-table-2" tabindex="0" id="sample-table-2_next"><a href="javascript:fyjz(${pageutil.x_pageCount });">尾页</a></li>
				&nbsp;第<input type="text" id="fy_now" style="width:40px;" value="${pageutil.x_pageNow}" />页
				<button type="button" class="btn btn-success btn-sm" onclick="fyty()">确定</button>
			</ul>
		</div>
	</div>
</div>

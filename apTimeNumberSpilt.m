clear;
clc;
n=245;
for pro=1:2  %文件夹编号
    totalTime=zeros(n);
    totalNumber=zeros(n);
    for fil=1:297  %分文件，相加得到总文件,297为AP数目，需要进行更改
        %*********************计算apTime
        strTime= strcat ('F:\移动轨迹数据\timeDivide\apVisited', int2str(pro) ,'\apTime\apTime_', int2str(fil) , '.csv') ; % 连接字符串
        mTime=load(strTime,'r');%以只读方式打开文件
        assignin('base',['x' num2str(fil)],mTime);
        %disp(['x' num2str(i)]);
        eval(['totalTime','=','totalTime','+','x',num2str(fil),';']);
        %*********************计算apNumber
        strNumber= strcat ('F:\移动轨迹数据\timeDivide\apVisited', int2str(pro) ,'\apNumber\apNumber_', int2str(fil) , '.csv') ; % 连接字符串
        mNumber=load(strNumber,'r');%以只读方式打开文件
        assignin('base',['y' num2str(fil)],mNumber);
        %disp(['y' num2str(i)]);
        eval(['totalNumber','=','totalNumber','+','y',num2str(fil),';'])

    end
    a=sum(sum(totalTime));
    b=sum(sum(totalNumber));
%             if(sum(totalTime(i,:)~=0))
%                      totalTimeRate(i,j) = totalTime(i,j)/sum(totalTime(i,:));%每个数据除以所在行或列的和，得到概率
%             end
%             if(sum(totalNumber(i,:)~=0))
%                      totalNumberRate(i,j) = totalNumber(i,j)/sum(totalNumber(i,:));%每个数据除以所在行或列的和，得到概率
%             end

    totalTimeRate= totalTime/a;
    totalNumberRate = totalNumber/b;
    totalRate=totalTimeRate+0.5*totalNumberRate; 
    
    strTime2= strcat ('F:\test\apTotalTime2', int2str(pro) , '.txt') ; % 分段总相遇时间，更改后面的分段数字
    dlmwrite(strTime2, totalTime);  
    
    strNumber2= strcat ('F:\test\apTotalNumber2', int2str(pro) , '.txt') ; % 分段总相遇次数
    dlmwrite(strNumber2, totalNumber);
    
    strTimeRate= strcat ('F:\test\apTotalTimeRate2', int2str(pro) , '.txt') ; % 分段时间概率
    dlmwrite(strTimeRate, totalTimeRate); 
    
    strNumberRate= strcat ('F:\test\apTotalNumberRate2', int2str(pro) , '.txt') ; % 分段次数概率
    dlmwrite(strNumberRate, totalNumberRate); 
    
    strRN= strcat ('F:\test\apTotalRate2', int2str(pro) , '.txt') ; % 分段总概率
    dlmwrite(strRN, totalRate); 
end
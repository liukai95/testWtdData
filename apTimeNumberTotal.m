clear;
clc;
n=245;
totalTime=zeros(n);
totalNumber=zeros(n);
    for fil=1:297  %分文件，相加得到总文件,297为AP数目，需要进行更改
        %*********************计算apTime
        strTime= strcat ('F:\移动轨迹数据\apVisited\apTime\apTime_', int2str(fil) , '.csv') ; % 连接字符串
        mTime=load(strTime,'r');%以只读方式打开文件
        assignin('base',['x' num2str(fil)],mTime);
        %disp(['x' num2str(i)]);
        eval(['totalTime','=','totalTime','+','x',num2str(fil),';']);
        %*********************计算apNumber
        strNumber= strcat ('F:\移动轨迹数据\apVisited\apNumber\apNumber_', int2str(fil) , '.csv') ; % 连接字符串
        mNumber=load(strNumber,'r');%以只读方式打开文件
        assignin('base',['y' num2str(fil)],mNumber);
        %disp(['y' num2str(i)]);
        eval(['totalNumber','=','totalNumber','+','y',num2str(fil),';'])

    end
    a=sum(sum(totalTime));
    b=sum(sum(totalNumber));
        
    totalTimeRate = totalTime/sum(sum(totalTime));
    totalNumberRate = totalNumber/sum(sum(totalNumber));
    totalRate=totalTimeRate+0.5*totalNumberRate; 

    strTime2= strcat ('F:\test\apTotalTime.txt') ; % 总相遇时间
    dlmwrite(strTime2, totalTime);  
    
    strNumber2= strcat ('F:\test\apTotalNumber.txt') ; % 总相遇次数
    dlmwrite(strNumber2, totalNumber);
    
    strTimeRate= strcat ('F:\test\apTotalTimeRate.txt') ; % 时间概率
    dlmwrite(strTimeRate, totalTimeRate);  
    
    strNumberRate= strcat ('F:\test\apTotalNumberRate.txt') ; % 次数概率
    dlmwrite(strNumberRate, totalNumberRate); 
    strRN= strcat ('F:\test\apTotalRate.txt') ; % 总概率，求社团
    dlmwrite(strRN, totalRate); 
    
    %rateConnection=load('F:\test\apTotalRate.txt');
    %rateConnection(rateConnection~=0) = 1;%得到无权的矩阵，只要不为0,则记为1